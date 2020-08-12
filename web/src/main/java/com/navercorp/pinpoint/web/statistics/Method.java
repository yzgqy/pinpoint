package com.navercorp.pinpoint.web.statistics;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Auther: yaya
 * @Date: 2019/12/7 22:05
 * @Description:
 */
public class Method {
    private String name;

    private String className;

    private Set<String> SQLs = new HashSet<>();

    private Set<String> tables = new HashSet<>();

    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Method(String name) {
        this.name = name;
    }

    public Method(){
    }

    public Set<String> getSQLs() {
        return SQLs;
    }

    public List<String> getTables() {
        if(this.SQLs.size()==0)
            return null;
        MySQLUtil sqlUtil = new MySQLUtil();
        return  sqlUtil.getTable(new ArrayList<>(this.SQLs)).get("tables");
    }
//    public List<String> getFkeys() {
//        if(this.SQLs.size()==0)
//            return null;
//        MySQLUtil sqlUtil = new MySQLUtil();
//        return  sqlUtil.getTable(new ArrayList<>(this.SQLs)).get("fkey");
//    }

    public void setName(String name) {
        this.name = name;
    }

    public void addSQL(String SQL) {
        this.SQLs.add(SQL);
        this.tables.addAll(getTableName(SQL));
    }

    public void addSQLs(Set<String> SQLs) {
        this.SQLs.addAll(SQLs);
        for(String sql:SQLs){
            this.tables.addAll(getTableName(sql));
        }
    }

    public String getClassName(){
        String[] list = name.split("\\(");
        if(list.length==1)
            return name;
        int i = list[0].lastIndexOf(".");
        if(i == -1)
            return list[0];
        return list[0].substring(0,i);

    }

    public String getClassName(String methodName){
        String[] list = methodName.split("\\(");
        if(list.length==1)
            return methodName;
        int i = list[0].lastIndexOf(".");
        if(i == -1)
            return list[0];
        return list[0].substring(0,i);

    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Method{" +
                "name='" + name + '\'' +
                ", className='" + className + '\'' +
                ", SQLs=" + SQLs +
                ", tables=" + tables +
                ", index=" + index +
                '}';
    }

    public List<String> getTableName(String sql) {
        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
//        if(sql.startsWith("s")||sql.startsWith("S"))
//        Select selectStatement = (Select) statement;
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableList = tablesNamesFinder.getTableList(statement);
        return tableList;
    }
}
