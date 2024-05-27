package pers.hzf.auth2.demos.infra.po;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.hzf.auth2.demos.common.utils.JacksonUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author houzhifang
 * @date 2024/5/20 18:27
 */
@Data
public class BasePO implements Serializable {


    private static final long serialVersionUID = 2865317351194670935L;
    
    private Date createdAt;

    private Date updatedAt;

    /**
     * 删除时间
     */
    @TableLogic
    private LocalDateTime deletedAt;

    @Override
    public String toString() {
        return JacksonUtils.toJson(this);
    }
    
}
