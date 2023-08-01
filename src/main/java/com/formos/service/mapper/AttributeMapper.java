package com.formos.service.mapper;

import com.formos.config.Constants;
import com.formos.service.dto.clickup.AttributesDTO;
import com.formos.service.dto.clickup.TaskComments;
import com.formos.service.utils.CommonUtils;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class AttributeMapper {

    public AttributesDTO toAttributesDTO(TaskComments.Attributes attributes) {
        AttributesDTO attributesDTO = new AttributesDTO();
        if (Objects.nonNull(attributes)) {
            attributesDTO.setBlockId(CommonUtils.getOrDefault(attributes.blockId, null));
            attributesDTO.setBold(CommonUtils.getOrDefault(attributes.bold, null));
            attributesDTO.setItalic(CommonUtils.getOrDefault(attributes.italic, null));
            attributesDTO.setUnderline(CommonUtils.getOrDefault(attributes.underline, null));
            attributesDTO.setHeader(CommonUtils.getOrDefault(attributes.header, null));
            attributesDTO.setStrike(CommonUtils.getOrDefault(attributes.strike, null));
            attributesDTO.setList(Objects.nonNull(attributes.list) ? attributes.list.list : null);
            attributesDTO.setColorClass(CommonUtils.getOrDefault(attributes.color_class, null));
            attributesDTO.setLink(CommonUtils.getOrDefault(attributes.link, null));
            attributesDTO.setAlign(CommonUtils.getOrDefault(attributes.align, null));
            attributesDTO.setIndent(CommonUtils.getOrDefault(attributes.indent, null));
            attributesDTO.setCode(CommonUtils.getOrDefault(attributes.code, null));
            attributesDTO.setBlockQuote(CommonUtils.getOrDefault(attributes.blockQuote, null));
            attributesDTO.setAdvancedBannerColor(CommonUtils.getOrDefault(attributes.advancedBannerColor, null));
            if (Objects.nonNull(attributesDTO.getAdvancedBannerColor())) {
                attributesDTO.setAdvancedBannerBackgroundColor(CommonUtils.decreaseSaturation(attributesDTO.getAdvancedBannerColor()));
            }
            attributesDTO.setDataId(CommonUtils.getOrDefault(attributes.dataId, null));
            attributesDTO.setTableColWidth(Objects.nonNull(attributes.tableCol) ? attributes.tableCol.width : null);
            if (Objects.nonNull(attributes.tableCellLine)) {
                attributesDTO.setTableCellLineRow(attributes.tableCellLine.row);
                attributesDTO.setTableCellLineRowspan(attributes.tableCellLine.rowspan);
                attributesDTO.setTableCellLineColspan(attributes.tableCellLine.colspan);
                attributesDTO.setTableCellLineCell(attributes.tableCellLine.cell);
            }

            attributesDTO.setMultiple(
                Constants.CSS_ATTRIBUTES
                    .stream()
                    .map(cssAttribute -> getTargetFieldValue(attributes, cssAttribute))
                    .filter(Objects::nonNull)
                    .count() +
                (Objects.nonNull(attributesDTO.getList()) && !"ordered".equals(attributesDTO.getList()) ? 1 : 0) >
                1
            );
            attributesDTO.setEmpty(
                Constants.CSS_ATTRIBUTES
                    .stream()
                    .map(cssAttribute -> getTargetFieldValue(attributes, cssAttribute))
                    .allMatch(Objects::isNull) &&
                Objects.isNull(attributesDTO.getList())
            );
        }
        return attributesDTO;
    }

    private Object getTargetFieldValue(TaskComments.Attributes attributes, String cssAttribute) {
        try {
            return attributes.getClass().getField(cssAttribute).get(attributes);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
