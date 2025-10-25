package com.webrayan.bazaar.modules.sale.controller;

import com.webrayan.bazaar.modules.sale.service.CartService;
import com.webrayan.bazaar.modules.sale.entity.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sale/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String selectedAttributes) {
        
        CartItem cartItem = cartService.addToCart(userId, productId, quantity, selectedAttributes);
        return ResponseEntity.ok(cartItem);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Long userId) {
        List<CartItem> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }

    @PutMapping("/update-quantity")
    public ResponseEntity<CartItem> updateQuantity(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        
        CartItem cartItem = cartService.updateCartItemQuantity(userId, productId, quantity);
        if (cartItem == null) {
            return ResponseEntity.noContent().build(); // آیتم حذف شده
        }
        return ResponseEntity.ok(cartItem);
    }

    @PutMapping("/update-attributes")
    public ResponseEntity<CartItem> updateAttributes(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam String selectedAttributes) {
        
        CartItem cartItem = cartService.updateCartItemAttributes(userId, productId, selectedAttributes);
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFromCart(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/total/{userId}")
    public ResponseEntity<BigDecimal> getCartTotal(@PathVariable Long userId) {
        BigDecimal total = cartService.getCartTotal(userId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<Long> getCartItemCount(@PathVariable Long userId) {
        Long count = cartService.getCartItemCount(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/quantity/{userId}")
    public ResponseEntity<Long> getTotalQuantity(@PathVariable Long userId) {
        Long quantity = cartService.getTotalQuantity(userId);
        return ResponseEntity.ok(quantity);
    }

    @GetMapping("/summary/{userId}")
    public ResponseEntity<Map<String, Object>> getCartSummary(@PathVariable Long userId) {
        List<CartItem> items = cartService.getCartItems(userId);
        BigDecimal total = cartService.getCartTotal(userId);
        Long itemCount = cartService.getCartItemCount(userId);
        Long totalQuantity = cartService.getTotalQuantity(userId);
        
        Map<String, Object> summary = Map.of(
                "items", items,
                "total", total,
                "itemCount", itemCount,
                "totalQuantity", totalQuantity,
                "isEmpty", cartService.isCartEmpty(userId)
        );
        
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/check-product")
    public ResponseEntity<Boolean> isProductInCart(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        
        boolean inCart = cartService.isProductInCart(userId, productId);
        return ResponseEntity.ok(inCart);
    }

    @GetMapping("/item")
    public ResponseEntity<CartItem> getCartItem(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        
        CartItem cartItem = cartService.getCartItem(userId, productId);
        if (cartItem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cartItem);
    }

    @PostMapping("/sync-prices/{userId}")
    public ResponseEntity<String> syncCartPrices(@PathVariable Long userId) {
        cartService.syncCartPrices(userId);
        return ResponseEntity.ok("Cart prices synchronized successfully");
    }

    @PostMapping("/validate-stock/{userId}")
    public ResponseEntity<String> validateCartStock(@PathVariable Long userId) {
        cartService.validateCartStock(userId);
        return ResponseEntity.ok("Cart stock validated successfully");
    }

    // آمارها و گزارشات مدیریتی
    @GetMapping("/admin/most-added-products")
    public ResponseEntity<List<Object[]>> getMostAddedToCartProducts() {
        List<Object[]> products = cartService.getMostAddedToCartProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/admin/users-with-product/{productId}")
    public ResponseEntity<List<Long>> getUsersWithProductInCart(@PathVariable Long productId) {
        List<Long> userIds = cartService.getUsersWithProductInCart(productId);
        return ResponseEntity.ok(userIds);
    }

    @PostMapping("/admin/cleanup-old-items")
    public ResponseEntity<String> cleanupOldCartItems() {
        cartService.cleanupOldCartItems();
        return ResponseEntity.ok("Old cart items cleaned up successfully");
    }
}
