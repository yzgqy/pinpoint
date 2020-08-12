package com.navercorp.pinpoint.web.mytestOPAndBo;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.create.table.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: yaya
 * @Date: 2020/1/17 09:10
 * @Description:
 */
public class TestDDL {
        public static void main(String[] args) throws IOException, JSQLParserException {
            // 你的MySQL DDL路径
            String mysqlDDLPath = "/Users/yaya/Desktop/bs-project/jpetstore-6/spring-jpetstore/src/main/resources/database/H2-schema.sql";
            String dDLs = FileUtils.readFileToString(new File(mysqlDDLPath));

            System.out.println(dDLs);
            System.out.println("++++++++++开始转换SQL语句+++++++++++++");

            Statements statements = CCJSqlParserUtil.parseStatements(dDLs);

            statements.getStatements()
                    .stream().filter(statement -> (statement instanceof CreateTable))
                    .map(statement -> (CreateTable) statement).forEach(ct -> {
                        String tableName = ct.getTable().getName();
                        List<Index> indexes = ct.getIndexes();
                        if(indexes.size()>0)
                            for(Index index:indexes){
                                if(index instanceof ForeignKeyIndex){
                                    ForeignKeyIndex foreignKeyIndex = (ForeignKeyIndex)index;
                                    System.out.println(tableName+"---"+foreignKeyIndex.getTable().getName());
                                }
                            }
            });
        }

        /**
         * 获得注释的下标
         *
         * @param columnSpecStrings columnSpecStrings
         * @return 下标
         */
        private static int getCommentIndex(List<String> columnSpecStrings) {
            for (int i = 0; i < columnSpecStrings.size(); i++) {
                if ("COMMENT".equalsIgnoreCase(columnSpecStrings.get(i))) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 生成COMMENT语句
         *
         * @param table        表名
         * @param column       字段名
         * @param commentValue 描述文字
         * @return COMMENT语句
         */
        private static String genCommentSql(String table, String column, String commentValue) {
            return String.format("COMMENT ON COLUMN %s.%s IS %s", table, column, commentValue);
        }
}
