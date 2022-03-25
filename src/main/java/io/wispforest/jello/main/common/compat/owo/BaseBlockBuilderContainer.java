package io.wispforest.jello.main.common.compat.owo;

import io.wispforest.jello.api.dye.registry.builder.BaseBlockBuilder;
import io.wispforest.owo.registration.reflect.SimpleFieldProcessingSubject;

import java.lang.reflect.Field;

public interface BaseBlockBuilderContainer extends SimpleFieldProcessingSubject<BaseBlockBuilder> {
    @Override
    default Class<BaseBlockBuilder> getTargetFieldType() {
        return BaseBlockBuilder.class;
    }

    @Override
    default void processField(BaseBlockBuilder value, String identifier, Field field) {
        BaseBlockBuilder.registerAdditionalBlockBuilder(value);
    }
}
