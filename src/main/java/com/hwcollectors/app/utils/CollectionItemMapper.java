package com.hwcollectors.app.utils;

import com.hwcollectors.app.dto.CollectionItemDto;
import com.hwcollectors.app.model.CollectionItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CollectionItemMapper {

    public CollectionItemDto toDto(CollectionItem entity) {
        CollectionItemDto dto = new CollectionItemDto();
        dto.setId(entity.getId());
        dto.setCondition(entity.getCondition());
        dto.setAcquiredDate(entity.getAcquiredDate());

        if (entity.getHotwheel() != null) {
            dto.setHotwheelCode(entity.getHotwheel().getCode());
            dto.setHotwheelName(entity.getHotwheel().getName());
        }
        return dto;
    }

    public List<CollectionItemDto> toDtoList(List<CollectionItem> entities) {
        return entities.stream().map(this::toDto).toList();
    }
}

