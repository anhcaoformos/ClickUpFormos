package com.formos.service.dto.clickup;

import com.formos.config.Constants;
import com.formos.service.utils.CommonUtils;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Objects;

public class AttributesDTO {

    private String blockId;
    private Boolean bold;
    private Boolean italic;
    private Boolean underline;
    private Integer header;
    private Boolean strike;
    private String list;
    private String color_class;
    private String align;
    private Integer indent;
    private Boolean code;
    private Object blockQuote;
    private String advancedBannerColor;
    private String advancedBannerBackgroundColor;
    private Boolean isEmpty;
    private Boolean isMultiple;
    private String dataId;
    private String tableColWidth;
    private String tableCellLineRowspan;
    private String tableCellLineColspan;
    private String tableCellLineCell;

    public AttributesDTO(TaskComments.Attributes attributes) {
        if (Objects.nonNull(attributes)) {
            this.blockId = CommonUtils.getOrDefault(attributes.blockId, null);
            this.bold = CommonUtils.getOrDefault(attributes.bold, null);
            this.italic = CommonUtils.getOrDefault(attributes.italic, null);
            this.underline = CommonUtils.getOrDefault(attributes.underline, null);
            this.header = CommonUtils.getOrDefault(attributes.header, null);
            this.strike = CommonUtils.getOrDefault(attributes.strike, null);
            this.list = Objects.nonNull(attributes.list) ? attributes.list.list : null;
            this.color_class = CommonUtils.getOrDefault(attributes.color_class, null);
            this.align = CommonUtils.getOrDefault(attributes.align, null);
            this.indent = CommonUtils.getOrDefault(attributes.indent, null);
            this.code = CommonUtils.getOrDefault(attributes.code, null);
            this.blockQuote = CommonUtils.getOrDefault(attributes.blockQuote, null);
            this.advancedBannerColor = CommonUtils.getOrDefault(attributes.advancedBannerColor, null);
            if (Objects.nonNull(this.getAdvancedBannerColor())) {
                this.advancedBannerBackgroundColor = decreaseSaturation(this.advancedBannerColor);
            }
            this.dataId = CommonUtils.getOrDefault(attributes.dataId, null);
            this.tableColWidth = Objects.nonNull(attributes.tableCol) ? attributes.tableCol.width : null;
            if (Objects.nonNull(attributes.tableCellLine)) {
                this.tableCellLineRowspan = attributes.tableCellLine.rowspan;
                this.tableCellLineColspan = attributes.tableCellLine.colspan;
                this.tableCellLineCell = attributes.tableCellLine.cell;
            }

            this.isMultiple =
                Constants.CSS_ATTRIBUTES
                    .stream()
                    .map(cssAttribute -> getTargetFieldValue(attributes, cssAttribute))
                    .filter(Objects::nonNull)
                    .count() >
                1;
            this.isEmpty =
                Constants.CSS_ATTRIBUTES
                    .stream()
                    .map(cssAttribute -> getTargetFieldValue(attributes, cssAttribute))
                    .allMatch(Objects::isNull);
        }
    }

    private Object getTargetFieldValue(TaskComments.Attributes attributes, String cssAttribute) {
        try {
            return attributes.getClass().getField(cssAttribute).get(attributes);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public Boolean getBold() {
        return bold;
    }

    public void setBold(Boolean bold) {
        this.bold = bold;
    }

    public Boolean getItalic() {
        return italic;
    }

    public void setItalic(Boolean italic) {
        this.italic = italic;
    }

    public Boolean getUnderline() {
        return underline;
    }

    public void setUnderline(Boolean underline) {
        this.underline = underline;
    }

    public Integer getHeader() {
        return header;
    }

    public void setHeader(Integer header) {
        this.header = header;
    }

    public Boolean getStrike() {
        return strike;
    }

    public void setStrike(Boolean strike) {
        this.strike = strike;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getColor_class() {
        return color_class;
    }

    public void setColor_class(String color_class) {
        this.color_class = color_class;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public Integer getIndent() {
        return indent;
    }

    public void setIndent(Integer indent) {
        this.indent = indent;
    }

    public Boolean getCode() {
        return code;
    }

    public void setCode(Boolean code) {
        this.code = code;
    }

    public Object getBlockQuote() {
        return blockQuote;
    }

    public void setBlockQuote(Object blockQuote) {
        this.blockQuote = blockQuote;
    }

    public String getAdvancedBannerColor() {
        return advancedBannerColor;
    }

    public void setAdvancedBannerColor(String advancedBannerColor) {
        this.advancedBannerColor = advancedBannerColor;
    }

    public Boolean getEmpty() {
        return isEmpty;
    }

    public void setEmpty(Boolean empty) {
        isEmpty = empty;
    }

    public Boolean getMultiple() {
        return isMultiple;
    }

    public void setMultiple(Boolean multiple) {
        isMultiple = multiple;
    }

    public String getAdvancedBannerBackgroundColor() {
        return advancedBannerBackgroundColor;
    }

    public void setAdvancedBannerBackgroundColor(String advancedBannerBackgroundColor) {
        this.advancedBannerBackgroundColor = advancedBannerBackgroundColor;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getTableColWidth() {
        return tableColWidth;
    }

    public void setTableColWidth(String tableColWidth) {
        this.tableColWidth = tableColWidth;
    }

    public String getTableCellLineRowspan() {
        return tableCellLineRowspan;
    }

    public void setTableCellLineRowspan(String tableCellLineRowspan) {
        this.tableCellLineRowspan = tableCellLineRowspan;
    }

    public String getTableCellLineColspan() {
        return tableCellLineColspan;
    }

    public void setTableCellLineColspan(String tableCellLineColspan) {
        this.tableCellLineColspan = tableCellLineColspan;
    }

    public String getTableCellLineCell() {
        return tableCellLineCell;
    }

    public void setTableCellLineCell(String tableCellLineCell) {
        this.tableCellLineCell = tableCellLineCell;
    }

    private Object getFieldValue(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Color getColorFromName(String colorName) {
        try {
            return (Color) Color.class.getField(colorName.toLowerCase()).get(null);
        } catch (Exception e) {
            return null;
        }
    }

    private String decreaseSaturation(String colorName) {
        Color color = getColorFromName(colorName);
        if (Objects.isNull(color)) {
            System.out.println("Invalid color name.");
            return "white";
        }
        float factor = 0.9f;
        float[] hsl = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsl);
        hsl[1] = Math.max(0f, hsl[1] - factor);
        Color modifiedColor = new Color(Color.HSBtoRGB(hsl[0], hsl[1], hsl[2]));
        return "#" + Integer.toHexString(modifiedColor.getRGB()).substring(2);
    }
}
