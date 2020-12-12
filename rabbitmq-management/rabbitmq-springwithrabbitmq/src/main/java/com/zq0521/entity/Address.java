package com.zq0521.entity;

import lombok.*;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String provinces;

    private String city;

    private String area;

}
