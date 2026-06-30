package com.baidu.myshop.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FavoriteVO {

    // Fields from Favorite entity
    private Integer favoriteId;
    private Integer uid;
    private Integer pid;
    private LocalDateTime favoriteTime;
    private Integer quantity; // Assuming quantity is part of favorite, as per t_favorite table

    // Fields from Product entity
    private String pname;
    private BigDecimal price;
    private String pfile;
    // private String description; // Decide if description is needed in the favorite list

    // You might want a constructor for easy creation
    public FavoriteVO() {
    }
} 