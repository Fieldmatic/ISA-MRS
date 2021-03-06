package mrsisa.project.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookableCalendarDTO {

    private String title;
    private String start;
    private String end;
    private String color;
    private String client;

}
