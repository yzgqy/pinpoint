package com.navercorp.pinpoint.web.mytestOPAndBo;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yaya
 * @Date: 2019/12/13 09:13
 * @Description:
 */
public class TestSQL {
    static Map<String, String> tableMap = new HashMap<>();

    public static void main(String[] args) throws Exception {
//        String sql = "select handlingev0_.id as id1_2_, handlingev0_.voyage_id as voyage_i2_2_, handlingev0_.location_id as location3_2_, handlingev0_.cargo_id as cargo_id4_2_, handlingev0_.completionTime as completi5_2_, handlingev0_.registrationTime as registra6_2_, handlingev0_.type as type7_2_ from HandlingEvent handlingev0_ cross join Cargo cargo1_ where handlingev0_.cargo_id=cargo1_.id and cargo1_.tracking_id=?";
//        Statement statement = CCJSqlParserUtil.parse(sql);
//        Select selectStatement = (Select) statement;
//        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
//        List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
//        System.out.println(tableList);

//        String sql ="select t1.a,t2.b from table1 as t1,table2 as t2,table3 t3 where t1.a = t2.b and t2.c=t3.d and t1.a = ?";

//        List<String> allSQLs = readFile02("/Users/yaya/Desktop/allSQL.txt");
        List<String> allSQLs =new ArrayList<>();
        allSQLs.add("select owner0_.id as id1_0_0_, pets1_.id as id1_1_1_, owner0_.first_name as first_na2_0_0_, owner0_.last_name as last_nam3_0_0_, owner0_.address as address4_0_0_, owner0_.city as city5_0_0_, owner0_.telephone as telephon6_0_0_, pets1_.name as name2_1_1_, pets1_.birth_date as birth_da3_1_1_, pets1_.owner_id as owner_id4_1_1_, pets1_.type_id as type_id5_1_1_, pets1_.owner_id as owner_id4_1_0__, pets1_.id as id1_1_0__ from owners owner0_ left outer join pets pets1_ on owner0_.id=pets1_.owner_id where owner0_.id=?");
//        allSQLs.add("select visit0_.id as id1_6_, visit0_.visit_date as visit_da2_6_, visit0_.description as descript3_6_, visit0_.pet_id as pet_id4_6_ from visits visit0_ where visit0_.pet_id=?");
//        allSQLs.add("select pettype0_.id as id1_3_0_, pettype0_.name as name2_3_0_ from types pettype0_ where pettype0_.id=?");
        allSQLs.add("UPDATE SEQUENCE SET NEXTID = 1 WHERE NAME = 2");
        List<TableAssociation> tableAssociations = new ArrayList<>();

        List<String> tableNameLine = new ArrayList<>();

        for(String sql:allSQLs) {
            getTableRelation(sql, tableAssociations);
            List<String> tables = getTableName(sql);
            String lineT = "";
            for(String x:tables){
                lineT=lineT+x+",";
            }
            tableNameLine.add(lineT);
        }
        for(TableAssociation tableAssociation:tableAssociations) {
            System.out.println(tableAssociation.toString());
        }
        List<String> fkeyLine = new ArrayList<>();

        for(TableAssociation tableAssociation:tableAssociations){
            String line = tableAssociation.getLeftTable()+"."+tableAssociation.getLeftColumn()
                    +"="+tableAssociation.getRightTable()+"."+ tableAssociation.getRightColumn()+"@@@";
            fkeyLine.add(line);
            System.out.println(line);
        }

//        writeFile02(fkeyLine,"/Users/yaya/Desktop/foreign_key.txt");
//        writeFile02(tableNameLine,"/Users/yaya/Desktop/tablenames.txt");
    }

    public static List<String> getTableName(String sql) {
        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
//        Select selectStatement = (Select) statement;
//        Update selectStatement = (Update) statement;
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableList = tablesNamesFinder.getTableList(statement);
        return tableList;
    }

    public static void getTableRelation(String sql,List<TableAssociation> tableAssociations) throws Exception {
        Statement stmt = CCJSqlParserUtil.parse(sql);
        if (stmt instanceof Select) {
            Select select = (Select) stmt;
            SelectBody selectBody = select.getSelectBody();
            alise(selectBody,tableAssociations);
        }
    }

    private static void alise(SelectBody selectBody,List<TableAssociation> tableAssociations) {
        if (selectBody instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) selectBody;
            FromItem fromItem = plainSelect.getFromItem();
            List<Join> joins = plainSelect.getJoins();

            if (fromItem instanceof Table) {
                Table table = (Table) fromItem;
                tableMap.put(table.getAlias().getName(), table.getName());
            }else if(fromItem instanceof SubSelect) {
                SubSelect subSelect = (SubSelect) fromItem;
                SelectBody subSelectBody = subSelect.getSelectBody();
                alise(subSelectBody,tableAssociations);
            }

            if(joins != null) {
                for (Join join : joins) {
                    Expression onExpression = join.getOnExpression();
                    FromItem rightItem = join.getRightItem();
                    if (rightItem instanceof Table) {
                        Table table = (Table) rightItem;
                        tableMap.put(table.getAlias().getName(), table.getName());
                    }
                    doExpression(onExpression,tableAssociations);
                }
            }

            doExpression(plainSelect.getWhere(),tableAssociations);
        }else if(selectBody instanceof SetOperationList) {
            SetOperationList setOperationList = (SetOperationList) selectBody;
            List<SelectBody> selects = setOperationList.getSelects();
            for (SelectBody selectBody3 : selects) {
                alise(selectBody3,tableAssociations);
            }
        }
    }

    private static void doExpression(Expression expression,List<TableAssociation> tableAssociations) {
        if (expression instanceof EqualsTo) {
            EqualsTo equalsTo = (EqualsTo) expression;
            Expression rightExpression = equalsTo.getRightExpression();
            Expression leftExpression = equalsTo.getLeftExpression();
            if (rightExpression instanceof Column && leftExpression instanceof Column) {
                Column rightColumn = (Column) rightExpression;
                Column leftColumn = (Column) leftExpression;
                tableAssociations.add(new TableAssociation(
                        tableMap.get(rightColumn.getTable().toString()),
                        rightColumn.getColumnName(),
                        tableMap.get(leftColumn.getTable().toString()),
                        leftColumn.getColumnName()));
                System.out.println(tableMap.get(rightColumn.getTable().toString()) + "表的" + rightColumn.getColumnName() + "字段 -> "
                        + tableMap.get(leftColumn.getTable().toString()) + "表的" + leftColumn.getColumnName() + "字段");
            }
        }else if(expression instanceof AndExpression){
            AndExpression andExpression = (AndExpression) expression;
            Expression leftExpression = andExpression.getLeftExpression();
            doExpression(leftExpression,tableAssociations);
            Expression rightExpression = andExpression.getRightExpression();
            doExpression(rightExpression,tableAssociations);
        }
    }


    public static List<String> readFile02(String path) throws IOException {
        // 使用一个字符串集合来存储文本中的路径 ，也可用String []数组
        List<String> list = new ArrayList<String>();
        FileInputStream fis = new FileInputStream(path);
        // 防止路径乱码 如果utf-8 乱码 改GBK eclipse里创建的txt 用UTF-8，在电脑上自己创建的txt 用GBK
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line = "";
        while ((line = br.readLine()) != null) {
            // 如果 t x t文件里的路径 不包含---字符串 这里是对里面的内容进行一个筛选
            // if (line.lastIndexOf("---") < 0) {
            list.add(line);
            // }
        }
        br.close();
        isr.close();
        fis.close();
        return list;
    }

    public static void writeFile02(List<String> arrs, String path) throws IOException {

        // 写入中文字符时解决中文乱码问题
        FileOutputStream fos = new FileOutputStream(new File(path));
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        BufferedWriter bw = new BufferedWriter(osw);

        for (String arr : arrs) {
            bw.write(arr + "\n");
        }

        // 注意关闭的先后顺序，先打开的后关闭，后打开的先关闭
        bw.close();
        osw.close();
        fos.close();
    }

}
