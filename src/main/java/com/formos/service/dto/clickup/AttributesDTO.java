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
    private String link;
    private String colorClass;
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
    private String tableCellLineRow;
    private String tableCellLineRowspan;
    private String tableCellLineColspan;
    private String tableCellLineCell;

    public AttributesDTO() {}

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getColorClass() {
        return colorClass;
    }

    public void setColorClass(String colorClass) {
        this.colorClass = colorClass;
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

    public String getTableCellLineRow() {
        return tableCellLineRow;
    }

    public void setTableCellLineRow(String tableCellLineRow) {
        this.tableCellLineRow = tableCellLineRow;
    }
}
