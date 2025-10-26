-- Database Indexes for Search Performance Optimization
-- Iran ECommerce Project - Search Module

-- ====================================
-- FULL-TEXT SEARCH INDEXES
-- ====================================

-- PostgreSQL Full-text search index for ads
CREATE INDEX CONCURRENTLY idx_ads_fulltext_search 
ON ads USING gin(to_tsvector('english', title || ' ' || COALESCE(description, '')));

-- PostgreSQL Full-text search index for products
CREATE INDEX CONCURRENTLY idx_products_fulltext_search 
ON catalog_products USING gin(to_tsvector('english', name || ' ' || COALESCE(short_description, '') || ' ' || COALESCE(description, '')));

-- ====================================
-- PERFORMANCE INDEXES FOR ADS
-- ====================================

-- Composite index for ads search with filters
CREATE INDEX CONCURRENTLY idx_ads_search_filters 
ON ads (status, is_active, is_featured, category_id, location_id);

-- Price range index for ads
CREATE INDEX CONCURRENTLY idx_ads_price_range 
ON ads (price) 
WHERE price IS NOT NULL AND is_active = true AND status = 'APPROVED';

-- Date-based index for recent ads
CREATE INDEX CONCURRENTLY idx_ads_created_at_desc 
ON ads (created_at DESC) 
WHERE is_active = true AND status = 'APPROVED';

-- Popularity index (views count)
CREATE INDEX CONCURRENTLY idx_ads_popularity 
ON ads (views_count DESC, created_at DESC) 
WHERE is_active = true AND status = 'APPROVED';

-- Category-specific index
CREATE INDEX CONCURRENTLY idx_ads_category_active 
ON ads (category_id, created_at DESC) 
WHERE is_active = true AND status = 'APPROVED';

-- Location-specific index
CREATE INDEX CONCURRENTLY idx_ads_location_active 
ON ads (location_id, created_at DESC) 
WHERE is_active = true AND status = 'APPROVED';

-- Featured ads index
CREATE INDEX CONCURRENTLY idx_ads_featured 
ON ads (is_featured, created_at DESC) 
WHERE is_active = true AND status = 'APPROVED' AND is_featured = true;

-- User ads index
CREATE INDEX CONCURRENTLY idx_ads_user_status 
ON ads (user_id, status, created_at DESC);

-- ====================================
-- PERFORMANCE INDEXES FOR PRODUCTS
-- ====================================

-- Composite index for products search with filters
CREATE INDEX CONCURRENTLY idx_products_search_filters 
ON catalog_products (status, is_active, is_featured, category_id);

-- Price range index for products
CREATE INDEX CONCURRENTLY idx_products_price_range 
ON catalog_products (price) 
WHERE price IS NOT NULL AND is_active = true AND status = 'PUBLISHED';

-- Stock-aware price index
CREATE INDEX CONCURRENTLY idx_products_price_stock 
ON catalog_products (price, stock_quantity) 
WHERE is_active = true AND status = 'PUBLISHED' AND stock_quantity > 0;

-- Date-based index for recent products
CREATE INDEX CONCURRENTLY idx_products_created_at_desc 
ON catalog_products (created_at DESC) 
WHERE is_active = true AND status = 'PUBLISHED';

-- Popularity index (views and sales)
CREATE INDEX CONCURRENTLY idx_products_popularity 
ON catalog_products (views_count DESC, sales_count DESC, created_at DESC) 
WHERE is_active = true AND status = 'PUBLISHED';

-- Best sellers index
CREATE INDEX CONCURRENTLY idx_products_bestsellers 
ON catalog_products (sales_count DESC, views_count DESC) 
WHERE is_active = true AND status = 'PUBLISHED' AND stock_quantity > 0;

-- Category-specific index
CREATE INDEX CONCURRENTLY idx_products_category_active 
ON catalog_products (category_id, created_at DESC) 
WHERE is_active = true AND status = 'PUBLISHED';

-- Featured products index
CREATE INDEX CONCURRENTLY idx_products_featured 
ON catalog_products (is_featured, created_at DESC) 
WHERE is_active = true AND status = 'PUBLISHED' AND is_featured = true;

-- Discounted products index
CREATE INDEX CONCURRENTLY idx_products_discounted 
ON catalog_products (discount_price, price, created_at DESC) 
WHERE is_active = true AND status = 'PUBLISHED' AND discount_price IS NOT NULL AND discount_price < price;

-- SKU search index
CREATE INDEX CONCURRENTLY idx_products_sku_search 
ON catalog_products USING gin(sku gin_trgm_ops);

-- Seller products index
CREATE INDEX CONCURRENTLY idx_products_seller_status 
ON catalog_products (seller_id, status, created_at DESC);

-- Stock management index
CREATE INDEX CONCURRENTLY idx_products_stock_management 
ON catalog_products (stock_quantity, min_stock_level) 
WHERE is_active = true AND status = 'PUBLISHED';

-- ====================================
-- SUPPORTING TABLES INDEXES
-- ====================================

-- Ad categories hierarchy index
CREATE INDEX CONCURRENTLY idx_ad_categories_parent 
ON ad_categories (parent_id, name);

-- Product categories hierarchy index  
CREATE INDEX CONCURRENTLY idx_catalog_categories_parent 
ON catalog_categories (parent_id, name);

-- Ad images index
CREATE INDEX CONCURRENTLY idx_ad_images_ad 
ON ad_images (ad_id);

-- Product images index
CREATE INDEX CONCURRENTLY idx_product_images_product 
ON product_images (product_id, is_primary);

-- Ad tags index for search
CREATE INDEX CONCURRENTLY idx_ad_tags_search 
ON ad_tags (tag_id, ad_id);

-- Ad attributes for filtering
CREATE INDEX CONCURRENTLY idx_ad_attribute_values_search 
ON ad_attribute_values (attribute_id, value, ad_id);

-- Product attributes for filtering
CREATE INDEX CONCURRENTLY idx_product_attribute_values_search 
ON product_attribute_values (attribute_id, value, product_id);

-- Ad favorites for user experience
CREATE INDEX CONCURRENTLY idx_ad_favorites_user 
ON ad_favorites (user_id, created_at DESC);

-- Ad reviews for ratings
CREATE INDEX CONCURRENTLY idx_ad_reviews_rating 
ON ad_reviews (ad_id, rating, created_at DESC);

-- Locations for geographic search
CREATE INDEX CONCURRENTLY idx_locations_name_search 
ON locations USING gin(name gin_trgm_ops);

-- Users for seller information
CREATE INDEX CONCURRENTLY idx_users_active_sellers 
ON users (is_active, created_at) 
WHERE is_active = true;

-- ====================================
-- TRIGRAM INDEXES FOR FUZZY SEARCH
-- ====================================

-- Enable pg_trgm extension if not already enabled
-- CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Trigram index for ads title fuzzy search
CREATE INDEX CONCURRENTLY idx_ads_title_trgm 
ON ads USING gin(title gin_trgm_ops);

-- Trigram index for products name fuzzy search
CREATE INDEX CONCURRENTLY idx_products_name_trgm 
ON catalog_products USING gin(name gin_trgm_ops);

-- ====================================
-- STATISTICS UPDATE
-- ====================================

-- Update table statistics for better query planning
ANALYZE ads;
ANALYZE catalog_products;
ANALYZE ad_categories;
ANALYZE catalog_categories;
ANALYZE locations;
ANALYZE users;

-- ====================================
-- MATERIALIZED VIEWS FOR AGGREGATIONS
-- ====================================

-- Popular categories materialized view
CREATE MATERIALIZED VIEW mv_popular_ad_categories AS
SELECT 
    ac.id,
    ac.name,
    COUNT(a.id) as ads_count,
    SUM(a.views_count) as total_views,
    AVG(a.price) as avg_price,
    MAX(a.created_at) as latest_ad
FROM ad_categories ac
LEFT JOIN ads a ON ac.id = a.category_id 
    AND a.is_active = true 
    AND a.status = 'APPROVED'
    AND a.created_at >= (CURRENT_DATE - INTERVAL '30 days')
GROUP BY ac.id, ac.name
ORDER BY ads_count DESC, total_views DESC;

CREATE UNIQUE INDEX idx_mv_popular_ad_categories_id ON mv_popular_ad_categories (id);

-- Popular product categories materialized view
CREATE MATERIALIZED VIEW mv_popular_product_categories AS
SELECT 
    cc.id,
    cc.name,
    COUNT(p.id) as products_count,
    SUM(p.views_count) as total_views,
    SUM(p.sales_count) as total_sales,
    AVG(p.price) as avg_price,
    MAX(p.created_at) as latest_product
FROM catalog_categories cc
LEFT JOIN catalog_products p ON cc.id = p.category_id 
    AND p.is_active = true 
    AND p.status = 'PUBLISHED'
    AND p.created_at >= (CURRENT_DATE - INTERVAL '30 days')
GROUP BY cc.id, cc.name
ORDER BY products_count DESC, total_sales DESC;

CREATE UNIQUE INDEX idx_mv_popular_product_categories_id ON mv_popular_product_categories (id);

-- Search statistics materialized view
CREATE MATERIALIZED VIEW mv_search_statistics AS
SELECT 
    'ads' as entity_type,
    COUNT(*) as total_count,
    COUNT(CASE WHEN status = 'APPROVED' THEN 1 END) as active_count,
    COUNT(CASE WHEN is_featured = true THEN 1 END) as featured_count,
    COUNT(CASE WHEN created_at >= (CURRENT_DATE - INTERVAL '7 days') THEN 1 END) as recent_count,
    AVG(views_count) as avg_views,
    MAX(views_count) as max_views
FROM ads
WHERE is_active = true

UNION ALL

SELECT 
    'products' as entity_type,
    COUNT(*) as total_count,
    COUNT(CASE WHEN status = 'PUBLISHED' THEN 1 END) as active_count,
    COUNT(CASE WHEN is_featured = true THEN 1 END) as featured_count,
    COUNT(CASE WHEN created_at >= (CURRENT_DATE - INTERVAL '7 days') THEN 1 END) as recent_count,
    AVG(views_count) as avg_views,
    MAX(views_count) as max_views
FROM catalog_products
WHERE is_active = true;

-- ====================================
-- REFRESH MATERIALIZED VIEWS FUNCTION
-- ====================================

CREATE OR REPLACE FUNCTION refresh_search_materialized_views()
RETURNS void AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_popular_ad_categories;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_popular_product_categories;
    REFRESH MATERIALIZED VIEW mv_search_statistics;
END;
$$ LANGUAGE plpgsql;

-- ====================================
-- PERFORMANCE MONITORING VIEWS
-- ====================================

-- Slow queries view for monitoring
CREATE VIEW v_search_performance_monitoring AS
SELECT 
    schemaname,
    tablename,
    attname,
    n_distinct,
    correlation,
    most_common_vals
FROM pg_stats 
WHERE schemaname = 'public' 
AND tablename IN ('ads', 'catalog_products', 'ad_categories', 'catalog_categories')
ORDER BY tablename, attname;

-- Index usage statistics
CREATE VIEW v_index_usage_stats AS
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_tup_read,
    idx_tup_fetch,
    idx_scan
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
AND tablename IN ('ads', 'catalog_products')
ORDER BY idx_scan DESC;

-- ====================================
-- SEARCH OPTIMIZATION NOTES
-- ====================================

/*
Performance Optimization Guidelines:

1. Full-text Search:
   - Uses PostgreSQL built-in text search with GIN indexes
   - Supports English language tokenization
   - Can be extended to support Persian/Farsi if needed

2. Index Strategy:
   - Composite indexes for common filter combinations
   - Partial indexes to reduce size and improve performance
   - CONCURRENTLY creation to avoid table locks

3. Materialized Views:
   - Pre-computed aggregations for dashboard/statistics
   - Should be refreshed periodically (daily/hourly)
   - Significant performance improvement for read-heavy operations

4. Monitoring:
   - Use pg_stat_user_indexes to monitor index usage
   - Use EXPLAIN ANALYZE for query optimization
   - Consider pg_stat_statements extension for query analysis

5. Maintenance:
   - Regular VACUUM and ANALYZE operations
   - Monitor index bloat
   - Consider partitioning for very large tables

6. Migration to Elasticsearch:
   - Current setup provides solid foundation
   - Database search can handle up to ~100K records efficiently
   - Elasticsearch migration path is ready when needed
*/
