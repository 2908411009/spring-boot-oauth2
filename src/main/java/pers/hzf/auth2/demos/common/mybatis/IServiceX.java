package pers.hzf.auth2.demos.common.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author houzhifang
 * @date 2024/5/21 10:42
 * 在 MyBatis Plus 的 IService 的基础上拓展，提供更多的能力
 */
public interface IServiceX<T> extends IService<T> {

    default T getOne(SFunction<T, ?> field, Object value) {
        return getOne(new LambdaQueryWrapper<T>().eq(field, value));
    }

    default List<T> list(SFunction<T, ?> field, Object value) {
        return list(new LambdaQueryWrapper<T>().eq(field, value));
    }

    default Integer count(SFunction<T, ?> field, Object value) {
        return count(new LambdaQueryWrapper<T>().eq(field, value));
    }

}
