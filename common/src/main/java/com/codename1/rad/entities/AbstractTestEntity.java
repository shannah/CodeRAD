package com.codename1.rad.entities;
import com.codename1.rad.annotations.RAD;

import com.codename1.rad.models.BaseEntity;
import com.codename1.rad.schemas.Thing;


@RAD
public abstract class AbstractTestEntity extends BaseEntity implements TestEntity {
    
    
    
    @RAD(tag="identifier")
    public abstract String getIdentifier();
    
   

}
