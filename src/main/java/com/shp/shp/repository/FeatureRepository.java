package com.shp.shp.repository;

import com.shp.shp.entity.Cart;
import com.shp.shp.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureRepository extends JpaRepository<Feature, Long> {

}
