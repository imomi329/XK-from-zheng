package com.xk.interceptor;

import com.xk.util.Paginator;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;


/**
 * 分頁攔截器
 * @author shuzheng
 * @date 2016年7月6日 下午6:04:12
 */
@Intercepts({
	@Signature(
		type = StatementHandler.class,
		method = "prepare",
		args = {Connection.class }
	) 
})
public class PageInterceptor implements Interceptor {

	private String pageSqlId;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		// 攔截的對象
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		// 通過反射拿到攔截對象的sqlid
		MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY);
		MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
		// 配置文件中SQL語句的ID
		String id = mappedStatement.getId();
		// 如果SQL的ID符合我們的過濾正則表達式
		if (id.matches(pageSqlId)) {
			BoundSql boundSql = statementHandler.getBoundSql();
			// 原始的SQL語句
			String sql = boundSql.getSql();
			Map<?, ?> parameter = (Map<?, ?>) boundSql.getParameterObject();
			// 有參數對象才能進行
			if (parameter != null) {
				Paginator paginator = (Paginator) parameter.get("paginator");
				// 有分頁對象，則改造危殆分頁查詢的SQL語句
				if (paginator != null) {
					String pageSql = sql + " limit " + (paginator.getPage() - 1) * paginator.getRows() + "," + paginator.getRows();
					metaObject.setValue("delegate.boundSql.sql", pageSql);
				}
			}
		}
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		// 攔截的對象是否需要代理，如果是則執行intercept方法增强，不是則直接放行
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		// 初始化攔截器配置
		this.pageSqlId = properties.getProperty("pageSqlId");
	}

}
