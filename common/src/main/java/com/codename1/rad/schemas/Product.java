/*
 * Copyright 2020 shannah.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codename1.rad.schemas;

import com.codename1.rad.models.Tag;

/**
 * From https://schema.org/Product
 * @author shannah
 */
public interface Product extends Thing {
    public static final Tag aggregateRating = new Tag("aggregateRating"),
            audience = new Tag("audience"),
            award = new Tag("award"),
            brand = new Tag("brand"),
            category = new Tag("category"),
            color = new Tag("color"),
            depth = new Tag("depth"),
            gtin = new Tag("gtin"),
            gtin12 = new Tag("gtin12"),
            gtin13 = new Tag("gtin13"),
            gtin8 = new Tag("gtin8"),
            hasEnergyConsumptionDetails = new Tag("hasEnergyConsumptionDetails"),
            hasMerchantReturnPolicy = new Tag("hasMerchantReturnPolicy"),
            height = new Tag("height"),
            inProductGroupWithID = new Tag("inProductGroupWithID"),
            isAccessoryOrSparePartFor = new Tag("isAccessoryOrSparePartFor"),
            isConsumableFor = new Tag("isConsumableFor"),
            isRelatedTo = new Tag("isRelatedTo"),
            isSimilarTo = new Tag("isSimilarTo"),
            isVariantOf = new Tag("isVariantOf"),
            itemCondition = new Tag("itemCondition"),
            logo = new Tag("logo"),
            manufacturer = new Tag("manufacturer"),
            material = new Tag("material"),
            model = new Tag("model"),
            mpn = new Tag("mpn"),
            nsn = new Tag("nsn"),
            offers = new Tag("offers"),
            pattern = new Tag("pattern"),
            productID = new Tag("productID"),
            productionDate = new Tag("productionDate"),
            purchaseDate = new Tag("purchaseDate"),
            releaseDate = new Tag("releaseDate"),
            review = new Tag("review"),
            size = new Tag("size"),
            sku = new Tag("sku"),
            slogan = new Tag("slogan"),
            width = new Tag("width");
            
            
            
            
            
}
