package com.example.server.repository;

import com.example.server.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query("""
    SELECT DISTINCT p FROM PostEntity p
    LEFT JOIN FETCH p.images
    JOIN p.room r
    WHERE p.status = 'PUBLISHED'
    
    AND (:location IS NULL OR 
         LOWER(p.address) LIKE LOWER(CONCAT('%', :location, '%')))
         
    AND (:maxPrice IS NULL OR p.price <= :maxPrice)
    
    AND (:area IS NULL OR p.area >= :area)
    
    AND r.status = 'EMPTY'
    
    ORDER BY p.createdAt DESC
""")
    List<PostEntity> searchPosts(
            @Param("location") String location,
            @Param("maxPrice") Double maxPrice,
            @Param("area") Double area
    );

    @Query("""
SELECT DISTINCT p FROM PostEntity p
LEFT JOIN FETCH p.images
WHERE p.user.username = :username
AND p.status = 'PUBLISHED'
ORDER BY p.createdAt DESC
""")
    List<PostEntity> findByUsername(@Param("username") String username);

    @Query("""
SELECT DISTINCT p FROM PostEntity p
LEFT JOIN FETCH p.images
WHERE p.status = 'PUBLISHED'
ORDER BY p.createdAt DESC
""")
    List<PostEntity> findAllWithImages();
}