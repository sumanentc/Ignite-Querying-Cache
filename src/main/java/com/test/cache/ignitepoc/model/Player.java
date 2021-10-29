package com.test.cache.ignitepoc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.cache.query.annotations.QueryTextField;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Player implements Serializable {
    private static final long serialVersionUID = 1389818123283358176L;
    @QuerySqlField(index = true)
    private Long id;
    @QueryTextField
    @QuerySqlField
    private String name;
    @QueryTextField
    @QuerySqlField(index = true)
    private String team;
    @QuerySqlField
    private double salary;
}
