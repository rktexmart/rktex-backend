package com.opsmonsters.quick_bite.services;

import com.opsmonsters.quick_bite.dto.PromoApplyRequest;
import com.opsmonsters.quick_bite.dto.PromoCodeDto;
import com.opsmonsters.quick_bite.dto.ResponseDto;
import com.opsmonsters.quick_bite.models.Cart;
import com.opsmonsters.quick_bite.models.PromoCode;
import com.opsmonsters.quick_bite.repositories.CartRepo;
import com.opsmonsters.quick_bite.repositories.ProductRepo;
import com.opsmonsters.quick_bite.repositories.PromoCodeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class PromoCodeService {

    @Autowired
    private PromoCodeRepo promoCodeRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private ProductRepo productRepo;

    @Value("${promo.config.path}")
    private String promoConfigPath;

    @Value("${image.upload.dir}")
    private String imageUploadDir;

    public void checkConfigPaths() {
        File imageDir = new File(imageUploadDir);
        File configFile = new File(promoConfigPath);

        if (!configFile.exists()) {
            System.out.println("⚠️ Config file not found at: " + configFile.getAbsolutePath());
        }
    }



    // ✅ Create Promo Code
    public PromoCode createPromoCode(PromoCodeDto promoCodeDto) {
        PromoCode promoCode = new PromoCode();
        promoCode.setPromoName(promoCodeDto.getPromoName());
        promoCode.setCouponAmount(promoCodeDto.getCouponAmount());
        promoCode.setMinOrderAmount(promoCodeDto.getMinOrderAmount());
        promoCode.setStartDate(promoCodeDto.getStartDate());
        promoCode.setExpiryDate(promoCodeDto.getExpiryDate());

        if (promoCodeDto.getProductId() != null) {
            productRepo.findById(promoCodeDto.getProductId())
                    .ifPresentOrElse(
                            promoCode::setProduct,
                            () -> {
                                throw new IllegalArgumentException("Invalid product ID: " + promoCodeDto.getProductId());
                            }
                    );
        }
        return promoCodeRepo.save(promoCode);
    }

    // ✅ Apply promo by Cart ID
    public ResponseDto applyPromoCode(Long cartId, String code) {
        Optional<Cart> cartOpt = cartRepo.findById(cartId);
        Optional<PromoCode> promoOpt = promoCodeRepo.findByPromoName(code);

        if (cartOpt.isEmpty() || promoOpt.isEmpty()) {
            return new ResponseDto(400, "Invalid cart or promo code.");
        }

        Cart cart = cartOpt.get();
        PromoCode promo = promoOpt.get();

        if (!promo.isValid()) {
            return new ResponseDto(400, "Promo code expired or not valid yet.");
        }

        if (cart.getPromoCode() != null) {
            return new ResponseDto(400, "Promo code already applied.");
        }

        if (promo.getMinOrderAmount() != null && cart.getGrandTotalPrice() < promo.getMinOrderAmount()) {
            return new ResponseDto(400, "Order amount is less than minimum required for this promo.");
        }

        double discountAmount = promo.getCouponAmount();
        if (discountAmount > cart.getGrandTotalPrice()) {
            discountAmount = cart.getGrandTotalPrice();
        }

        cart.setPromoCode(promo);
        cart.applyDiscount(discountAmount);
        cartRepo.save(cart);

        return new ResponseDto(200, "Promo code applied successfully!", discountAmount);
    }

    // ✅ Apply promo without cart (direct total + product check)
    public ResponseDto applyPromoWithoutCart(PromoApplyRequest req) {
        if (req == null || req.getPromoName() == null || req.getTotalAmount() == null) {
            return new ResponseDto(400, "Invalid request data", null);
        }

        Optional<PromoCode> promoOpt = promoCodeRepo.findByPromoName(req.getPromoName());

        if (promoOpt.isEmpty()) {
            return new ResponseDto(400, "Invalid promo code.", null);
        }

        PromoCode promo = promoOpt.get();

        if (!promo.isValid()) {
            return new ResponseDto(400, "Promo code expired or not valid yet.", null);
        }

        if (promo.getMinOrderAmount() != null && req.getTotalAmount() < promo.getMinOrderAmount()) {
            return new ResponseDto(400, "Order amount is below the minimum required.", null);
        }

        if (promo.getProduct() != null &&
                (req.getProductId() == null || !promo.getProduct().getProductId().equals(req.getProductId()))) {
            return new ResponseDto(400, "Promo code not valid for this product.", null);
        }

        double discountAmount = calculateDiscount(req.getTotalAmount(), promo);

        return new ResponseDto(200, "Promo applied successfully!", discountAmount);
    }


    // ✅ Apply promo for the whole cart (new method)
    // inside PromoCodeService

    public ResponseDto applyPromoForCart(PromoApplyRequest req) {
        Optional<PromoCode> promoOpt = promoCodeRepo.findByPromoName(req.getPromoName());

        if (promoOpt.isEmpty()) {
            return new ResponseDto(400, "Invalid promo code.");
        }

        PromoCode promo = promoOpt.get();

        if (!promo.isValid()) {
            return new ResponseDto(400, "Promo code expired or not valid yet.");
        }

        // ✅ If promo is product-specific, check if productIds in cart contain that product
        // If promo is product-specific → check match
        if (promo.getProduct() != null) {
            boolean match = false;
            if (req.getProductIds() != null) {
                match = req.getProductIds().contains(promo.getProduct().getProductId());
            } else if (req.getProductId() != null) {
                match = promo.getProduct().getProductId().equals(req.getProductId());
            }

            if (!match) {
                return new ResponseDto(400, "Promo code not valid for this product.");
            }
        }
// else → cart-level promo, no product check needed

// else → cart-level promo → no product check needed


        // ✅ Check minimum order condition
        if (promo.getMinOrderAmount() != null && req.getTotalAmount() < promo.getMinOrderAmount()) {
            return new ResponseDto(400, "Order amount is below the minimum required.");
        }

        // ✅ Calculate discount (flat or percentage)
        double discountAmount;
        boolean isPercentage = promo.getCouponAmount() > 0 && promo.getCouponAmount() <= 100;

        if (isPercentage) {
            discountAmount = req.getTotalAmount() * (promo.getCouponAmount() / 100.0);
            discountAmount = Math.round(discountAmount * 100.0) / 100.0;
        } else {
            discountAmount = promo.getCouponAmount();
        }

        // Prevent discount from exceeding total
        if (discountAmount > req.getTotalAmount()) {
            discountAmount = req.getTotalAmount();
        }

        return new ResponseDto(200, "Promo applied successfully!", discountAmount);
    }


    // ✅ Utility discount calculator
    private double calculateDiscount(Double totalAmount, PromoCode promo) {
        double discountAmount;

        // Auto-detect percentage vs flat
        if (promo.getCouponAmount() > 0 && promo.getCouponAmount() <= 100) {
            discountAmount = totalAmount * (promo.getCouponAmount() / 100.0);
        } else {
            discountAmount = promo.getCouponAmount();
        }

        if (discountAmount > totalAmount) {
            discountAmount = totalAmount;
        }

        return Math.round(discountAmount * 100.0) / 100.0;
    }

    // ✅ Get all promo codes
    public List<PromoCode> getAllPromoCodes() {
        return promoCodeRepo.findAll();
    }

    // ✅ Validate a promo code
    public boolean isPromoCodeValid(String promoName) {
        return promoCodeRepo.findByPromoName(promoName)
                .map(PromoCode::isValid)
                .orElse(false);
    }

    // ✅ Delete a promo code
    public void deletePromoCode(Long promoId) {
        promoCodeRepo.deleteById(promoId);
    }
}
