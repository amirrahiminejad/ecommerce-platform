package com.webrayan.store.modules.sale.service;

import com.webrayan.store.modules.sale.entity.CartItem;
import com.webrayan.store.modules.sale.repository.CartRepository;
import com.webrayan.store.modules.catalog.entity.Product;
import com.webrayan.store.modules.catalog.repository.ProductRepository;
import com.webrayan.store.modules.acl.entity.User;
import com.webrayan.store.modules.acl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public CartItem addToCart(Long userId, Long productId, Integer quantity, String selectedAttributes) {
        // بررسی وجود کاربر
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // بررسی وجود محصول
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // بررسی موجودی
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        // بررسی وجود آیتم در سبد
        Optional<CartItem> existingItem = cartRepository.findByUserIdAndProductId(userId, productId);
        
        if (existingItem.isPresent()) {
            // به‌روزرسانی تعداد
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            
            if (product.getStockQuantity() < newQuantity) {
                throw new RuntimeException("Cannot add more items. Total would exceed available stock.");
            }
            
            cartItem.updateQuantity(newQuantity);
            cartItem.setSelectedAttributes(selectedAttributes);
            return cartRepository.save(cartItem);
        } else {
            // ایجاد آیتم جدید
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getPrice());
            cartItem.setSelectedAttributes(selectedAttributes);
            
            return cartRepository.save(cartItem);
        }
    }

    @Transactional
    public CartItem updateCartItemQuantity(Long userId, Long productId, Integer newQuantity) {
        CartItem cartItem = cartRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (newQuantity <= 0) {
            cartRepository.delete(cartItem);
            return null;
        }

        // بررسی موجودی
        Product product = cartItem.getProduct();
        if (product.getStockQuantity() < newQuantity) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        cartItem.updateQuantity(newQuantity);
        cartItem.updatePrice(product.getPrice()); // به‌روزرسانی قیمت
        
        return cartRepository.save(cartItem);
    }

    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        cartRepository.removeItemFromCart(userId, productId);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartRepository.clearCartByUser(userId);
    }

    public List<CartItem> getCartItems(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public BigDecimal getCartTotal(Long userId) {
        BigDecimal total = cartRepository.getCartTotalByUser(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public Long getCartItemCount(Long userId) {
        Long count = cartRepository.getCartItemCountByUser(userId);
        return count != null ? count : 0L;
    }

    public Long getTotalQuantity(Long userId) {
        Long quantity = cartRepository.getTotalQuantityByUser(userId);
        return quantity != null ? quantity : 0L;
    }

    public boolean isProductInCart(Long userId, Long productId) {
        return cartRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public void syncCartPrices(Long userId) {
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (!item.getUnitPrice().equals(product.getPrice())) {
                item.updatePrice(product.getPrice());
                cartRepository.save(item);
            }
        }
    }

    @Transactional
    public void validateCartStock(Long userId) {
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
                // کاهش تعداد یا حذف آیتم
                if (product.getStockQuantity() > 0) {
                    item.updateQuantity(product.getStockQuantity());
                    cartRepository.save(item);
                } else {
                    cartRepository.delete(item);
                }
            }
        }
    }

    @Transactional
    public void cleanupOldCartItems() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30); // 30 روز
        cartRepository.deleteOldCartItems(cutoffDate);
    }

    public List<Object[]> getMostAddedToCartProducts() {
        return cartRepository.getMostAddedToCartProducts();
    }

    public List<Long> getUsersWithProductInCart(Long productId) {
        return cartRepository.getUsersWithProductInCart(productId);
    }

    @Transactional
    public CartItem updateCartItemAttributes(Long userId, Long productId, String selectedAttributes) {
        CartItem cartItem = cartRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartItem.setSelectedAttributes(selectedAttributes);
        return cartRepository.save(cartItem);
    }

    public boolean isCartEmpty(Long userId) {
        return getCartItemCount(userId) == 0;
    }

    public CartItem getCartItem(Long userId, Long productId) {
        return cartRepository.findByUserIdAndProductId(userId, productId)
                .orElse(null);
    }
}
