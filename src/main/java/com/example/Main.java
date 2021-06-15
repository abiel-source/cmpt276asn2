/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

@Controller
@SpringBootApplication
public class Main {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  // main page
  // =========
  @RequestMapping("/")
  String index(Map<String, Object> model) {
    return "index";
  }

  // create
  // ======
  @GetMapping(
    path = "/create"
  )
  public String getCreateForm(Map<String, Object> model)
  {
    Rectangle rect = new Rectangle();
    model.put("rect", rect);
    return "create";
  }

  @PostMapping(
    path = "/create",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )
  public String handleCreateForm(Map<String, Object> model, Rectangle rect) throws Exception
  {
    // save rectangle into db
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sqlTable = "CREATE TABLE IF NOT EXISTS rectangles (id serial, name varchar(20), color varchar(20), width integer, height integer)";
      String sqlInsert = "INSERT INTO rectangles (name, color, width, height) VALUES ('" + rect.getName() + "', '" + rect.getColor() + "', " + rect.getWidth() + ", " + rect.getHeight() + ")";
      stmt.executeUpdate(sqlTable);
      stmt.executeUpdate(sqlInsert);
      return "redirect:/create/success";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  // create success
  // ==============
  @GetMapping("/create/success")
  public String handleCreateSuccess()
  {
    return "create-success";
  }

  // destroy
  // =======
  @GetMapping(
    path = "/destroy"
  )
  public String getDestroyForm(Map<String, Object> model)
  {
    Rectangle rectID = new Rectangle();
    model.put("rectID", rectID);
    return "destroy";
  }

  @PostMapping(
    path = "/destroy",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )
  public String handleDestroyForm(Map<String, Object> model, Rectangle rectID) throws Exception
  {
    // delete rectangle with id = id from db
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sqlTable = "CREATE TABLE IF NOT EXISTS rectangles (id serial, name varchar(20), color varchar(20), width integer, height integer)";
      String sqlDelete = "DELETE FROM rectangles WHERE id=" + rectID.getID();
      stmt.executeUpdate(sqlTable);
      stmt.executeUpdate(sqlDelete);
      return "redirect:/destroy/success";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  // destroy success
  // ==============
  @GetMapping("/destroy/success")
  public String handleDestroySuccess()
  {
    return "destroy-success";
  }

  // display records
  // ===============
  @GetMapping(
    path = "/display"
  )
  public String getRectRecords(Map<String, Object> model)
  {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sqlTable = "CREATE TABLE IF NOT EXISTS rectangles (id serial, name varchar(20), color varchar(20), width integer, height integer)";
      String sqlSelect = "SELECT * FROM rectangles";
      stmt.executeUpdate(sqlTable);
      ResultSet rs = stmt.executeQuery(sqlSelect);

      // rectangles
      ArrayList<Rectangle> rects = new ArrayList<Rectangle>();
      while (rs.next())
      {
        Rectangle rect = new Rectangle();
        rect.setID(rs.getInt("id"));
        rect.setName(rs.getString("name"));
        rect.setColor(rs.getString("color"));
        rect.setWidth(rs.getInt("width"));
        rect.setHeight(rs.getInt("height"));
        rects.add(rect);
      }
      model.put("rects", rects);

      return "display";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  // display record details
  // ======================
  @GetMapping(
    path = "/display/details/{id}"
  )
  public String getRectRecordDetails(Map<String, Object> model, @PathVariable int id)
  {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sqlSelect = "SELECT * FROM rectangles WHERE id=" + id;
      ResultSet rs = stmt.executeQuery(sqlSelect);
      rs.next(); // position rs properly
      Rectangle rect = new Rectangle();
      rect.setID(rs.getInt("id"));
      rect.setName(rs.getString("name"));
      rect.setColor(rs.getString("color"));
      rect.setWidth(rs.getInt("width"));
      rect.setHeight(rs.getInt("height"));
      model.put("rect", rect);
      return "display-details";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  // config
  // ======
  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }
}
