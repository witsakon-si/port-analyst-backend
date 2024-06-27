package com.mport.dao;

import com.mport.domain.dto.SimpleDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class CommonDaoImpl implements CommonDao {
    private final JdbcTemplate defaultJdbcTemplate;

    public CommonDaoImpl(JdbcTemplate defaultJdbcTemplate) {
        this.defaultJdbcTemplate = defaultJdbcTemplate;
    }

    @Override
    public List<String> findAllType() {
        List<Object> param = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select type from history where active = true group by type order by type desc");

        List<String> data = defaultJdbcTemplate.query(sql.toString(), new RowMapper<String>(){
            public String mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                return rs.getString("type");
            }
        });
        return data;
    }

    @Override
    public List<String> findAllName() {
        List<Object> param = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select name from history where active = true group by name order by name asc");

        List<String> data = defaultJdbcTemplate.query(sql.toString(), new RowMapper<String>(){
            public String mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                return rs.getString("name");
            }
        });
        return data;
    }

    @Override
    public List<SimpleDTO> findAllSymbol() {
        List<Object> param = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct symbol as code, symbol as name from price_alert where active = 1");

        List<SimpleDTO> results = defaultJdbcTemplate.query(sql.toString(), param.toArray(), (rs, rowNum) -> {
            SimpleDTO obj = new SimpleDTO();
            obj.setCode(rs.getString("CODE"));
            obj.setName(rs.getString("NAME"));

            return obj;
        });

        return results;
    }

    @Override
    public List<SimpleDTO> findAllAcctGroup() {
        List<Object> param = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct acct_group as code, acct_group as name from onepa");

        List<SimpleDTO> results = defaultJdbcTemplate.query(sql.toString(), param.toArray(), (rs, rowNum) -> {
            SimpleDTO obj = new SimpleDTO();
            obj.setCode(rs.getString("CODE"));
            obj.setName(rs.getString("NAME"));

            return obj;
        });

        return results;
    }

    @Override
    public List<String> findAllGroupAndName() {
        List<Object> param = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select type||':'||name as name from history where active = true group by type, name order by type, name");

        List<String> data = defaultJdbcTemplate.query(sql.toString(), new RowMapper<String>(){
            public String mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                return rs.getString("name");
            }
        });
        return data;
    }
}
