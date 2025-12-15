package com.opsmonsters.quick_bite.services;

import com.opsmonsters.quick_bite.dto.CartDetailsDto;
import com.opsmonsters.quick_bite.dto.CartDto;
import com.opsmonsters.quick_bite.dto.ResponseDto;
import com.opsmonsters.quick_bite.models.*;
import com.opsmonsters.quick_bite.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartDetailsRepo cartDetailsRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PromoCodeRepo promoCodeRepo;

    public int countItemsInActiveCart(Long userId) {
        return userRepo.findById(userId)
                .flatMap(user -> cartRepo.findFirstByUserAndStatus(user, "ACTIVE"))
                .map(cart -> cart.getCartDetails().stream().mapToInt(CartDetails::getQuantity).sum())
                .orElse(0);
    }

    @Transactional
    public ResponseDto addToCart(CartDto cartDto) {
        Users user = userRepo.findById(cartDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        Product product = productRepo.findById(cartDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found!"));

        if (product.getStock() == null || product.getStock() < cartDto.getQuantity()) {
            return new ResponseDto(ResponseDto.Status.BAD_REQUEST.getCode(), "Not enough stock available!", null);
        }

        Cart cart = cartRepo.findFirstByUserAndStatus(user, "ACTIVE")
                .orElseGet(() -> cartRepo.save(new Cart(user, "ACTIVE")));

        Optional<CartDetails> existingCartDetail = cartDetailsRepo.findByCartAndProduct(cart, product);

        PromoCode promoCode = null;
        String promoName = cartDto.getPromoName();
        if (promoName != null && !promoName.isEmpty()) {
            promoCode = promoCodeRepo.findByPromoName(promoName).orElse(null);
        }

        if (existingCartDetail.isPresent()) {
            CartDetails cartDetail = existingCartDetail.get();
            int newQuantity = cartDetail.getQuantity() + cartDto.getQuantity();

            if (product.getStock() < newQuantity) {
                return new ResponseDto(ResponseDto.Status.BAD_REQUEST.getCode(), "Not enough stock available!", null);
            }

            cartDetail.setQuantity(newQuantity);
            if (cartDetail.getPromoCode() != null) {
                promoCode = cartDetail.getPromoCode();
            }
            cartDetail.setPromoCode(promoCode);

            double discount = (promoCode != null) ? promoCode.getCouponAmount() * newQuantity : 0.0;
            cartDetail.setDiscountAmount(discount);

            double totalPrice = (product.getPrice() * newQuantity) - discount;
            cartDetail.setTotalPrice(totalPrice);

            cartDetailsRepo.save(cartDetail);
        } else {
            double discount = (promoCode != null) ? promoCode.getCouponAmount() * cartDto.getQuantity() : 0.0;
            CartDetails cartDetails = new CartDetails(
                    cart, product, cartDto.getQuantity(), product.getPrice(), promoCode,
                    cartDto.getColor(), cartDto.getSize() // ✅ save color & size
            );
            cartDetails.setDiscountAmount(discount);
            double totalPrice = (product.getPrice() * cartDto.getQuantity()) - discount;
            cartDetails.setTotalPrice(totalPrice);

            cartDetailsRepo.save(cartDetails);
        }

        CartDetailsDto cartDetailsDto = buildCartDetailsDto(cart);
        return new ResponseDto(ResponseDto.Status.SUCCESS.getCode(), "Product added to cart!", cartDetailsDto);
    }


    @Transactional
    public String removeFromCart(Long cartDetailsId) {
        Optional<CartDetails> cartDetailOpt = cartDetailsRepo.findById(cartDetailsId);
        if (cartDetailOpt.isPresent()) {
            cartDetailsRepo.delete(cartDetailOpt.get());
            return "Product removed from cart!";
        }
        return "Cart item not found!";
    }

    @Transactional(readOnly = true)
    public CartDetailsDto getCartDetails(Long cartId) {
        return cartRepo.findById(cartId)
                .map(this::buildCartDetailsDto)
                .orElse(null);
    }

    public List<CartDto> getUserCarts(Long userId) {
        Users user = userRepo.findById(userId).orElse(null);
        if (user == null) return List.of();
        return cartRepo.findByUser(user).stream()
                .map(cart -> new CartDto(cart.getCartId(), user.getUserId(), cart.getStatus()))
                .collect(Collectors.toList());
    }

    @Transactional
    public String checkoutCart(Long cartId) {
        Optional<Cart> cartOpt = cartRepo.findById(cartId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cart.setStatus("CHECKED_OUT");
            cartRepo.save(cart);
            return "Cart checked out successfully!";
        }
        return "Cart not found!";
    }

    @Transactional(readOnly = true)
    private CartDetailsDto buildCartDetailsDto(Cart cart) {
        List<CartDetailsDto.ProductDetails> productDetailsList = cart.getCartDetails().stream()
                .map(detail -> new CartDetailsDto.ProductDetails(
                        detail.getCartDetailId(),
                        detail.getProduct().getImageFilename(),
                        detail.getProduct().getName(),
                        detail.getProduct().getDescription(),
                        detail.getProduct().getPrice(),
                        detail.getQuantity(),
                        detail.getPromoCode() != null ? detail.getPromoCode().getPromoName() : "",
                        detail.getDiscountAmount(),
                        detail.getColor(),
                        detail.getSize()
                ))
                .toList();

        double subTotal = productDetailsList.stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();

        double discountTotal = cart.getCartDetails().stream()
                .mapToDouble(CartDetails::getDiscountAmount)
                .sum();

        double tax = (subTotal - discountTotal) * 0.18;
        double total = (subTotal - discountTotal) + tax;

        return new CartDetailsDto(productDetailsList, subTotal, discountTotal, tax, total);
    }


    public List<CartDetailsDto> getCartItemsByUserId(Long userId) {
        Users user = userRepo.findById(userId).orElse(null);
        if (user == null) return List.of();

        List<Cart> userCarts = cartRepo.findByUser(user);

        return userCarts.stream()
                .filter(cart -> cart.getStatus().equalsIgnoreCase("ACTIVE"))
                .map(this::buildCartDetailsDto)
                .collect(Collectors.toList());
    }

    public CartDto getCartByUserId(Long userId) {
        Users user = userRepo.findById(userId).orElse(null);
        if (user == null) return new CartDto();

        Optional<Cart> optionalCart = cartRepo.findFirstByUserAndStatus(user, "ACTIVE");

        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            CartDto cartDto = new CartDto();
            cartDto.setCartId(cart.getCartId());
            cartDto.setUserId(user.getUserId());
            cartDto.setStatus(cart.getStatus());
            return cartDto;
        } else {
            return new CartDto();
        }
    }
}
