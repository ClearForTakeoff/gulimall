package com.common.to;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * spu信息
 * 
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 */
@Data
public class SpuInfoTo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */

	private Long id;
	/**
	 * 商品名称
	 */
	private String spuName;
	/**
	 * 商品描述
	 */
	private String spuDescription;
	/**
	 * 所属分类id
	 */
	private Long catelogId;
	/**
	 * 品牌id
	 */
	private Long brandId;
	private String brandName;
	private String spuDefaultImg;
	/**
	 * 
	 */
	private BigDecimal weight;
	/**
	 * 上架状态[0 - 下架，1 - 上架]
	 */
	private Integer publishStatus;
	/**
	 * 
	 */

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

}
