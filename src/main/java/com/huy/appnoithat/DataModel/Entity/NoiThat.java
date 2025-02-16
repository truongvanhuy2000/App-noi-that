package com.huy.appnoithat.DataModel.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class NoiThat implements NoiThatEntity {
    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("hangMuc")
    List<HangMuc> hangMucList;

    public void add(HangMuc hangMuc) {
        hangMucList.add(hangMuc);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
