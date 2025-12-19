package com.opsmonsters.quick_bite.controller;

import com.opsmonsters.quick_bite.dto.PromoApplyRequest;
import com.opsmonsters.quick_bite.dto.PromoCodeDto;
import com.opsmonsters.quick_bite.dto.ResponseDto;
import com.opsmonsters.quick_bite.models.PromoCode;
import com.opsmonsters.quick_bite.services.PromoCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {
        "http://localhost:4200",
        "http://13.61.26.222",
        "https://heartfelt-blancmange-2c2c34.netlify.app",
        "https://rktex-frontend.netlify.app"
})
@RequestMapping("/api/promo")
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    public PromoCodeController(PromoCodeService promoCodeService) {
        this.promoCodeService = promoCodeService;
    }

    // ✅ Create a new promo code
    @PostMapping("/create")
    public ResponseEntity<PromoCode> createPromoCode(@RequestBody PromoCodeDto promoCodeDto) {
        return ResponseEntity.ok(promoCodeService.createPromoCode(promoCodeDto));
    }

    // ✅ Apply a promo code to a cart
    @PostMapping("/apply/{cartId}/{promoName}")
    public ResponseEntity<ResponseDto> applyPromoCode(@PathVariable Long cartId, @PathVariable String promoName) {
        ResponseDto response = promoCodeService.applyPromoCode(cartId, promoName);
        HttpStatus status = HttpStatus.valueOf(response.getStatusCode());  // ✅ Fix: Convert status code to HttpStatus
        return ResponseEntity.status(status).body(response);
    }

    // ✅ Get all promo codes
    @GetMapping
    public ResponseEntity<List<PromoCode>> getAllPromoCodes() {
        return ResponseEntity.ok(promoCodeService.getAllPromoCodes());
    }

    // ✅ Validate a promo code
    @GetMapping("/validate/{promoName}")
    public ResponseEntity<Boolean> validatePromoCode(@PathVariable String promoName) {
        boolean isValid = promoCodeService.isPromoCodeValid(promoName);
        return ResponseEntity.ok(isValid);
    }

    // ✅ Delete a promo code
    @DeleteMapping("/admin/promo/delete/{promoId}")
    public ResponseEntity<String> deletePromoCode(@PathVariable Long promoId) {
        promoCodeService.deletePromoCode(promoId);
        return ResponseEntity.ok("Promo code deleted successfully.");
    }


//    @PostMapping("/apply-direct")
//    public ResponseEntity<ResponseDto> applyPromoWithoutCart(@RequestBody PromoApplyRequest request) {
//        ResponseDto response = promoCodeService.applyPromoWithoutCart(request);
//        return ResponseEntity.status(HttpStatus.valueOf(response.getStatusCode())).body(response);
//    }


    @PostMapping("/apply-direct")
    public ResponseEntity<ResponseDto> applyPromoWithoutCart(@RequestBody PromoApplyRequest request) {
        try {
            ResponseDto response = promoCodeService.applyPromoWithoutCart(request);
            return ResponseEntity.status(HttpStatus.valueOf(response.getStatusCode())).body(response);
        } catch (Exception e) {
            e.printStackTrace(); // exact backend error print
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(500, "Internal server error: " + e.getMessage(), null));
        }
    }


    // ✅ Apply promo code for whole cart
    @PostMapping("/apply-cart")
    public ResponseEntity<ResponseDto> applyPromoForCart(@RequestBody PromoApplyRequest request) {
        ResponseDto response = promoCodeService.applyPromoForCart(request);
        return ResponseEntity.status(HttpStatus.valueOf(response.getStatusCode())).body(response);
    }


}
