package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "position")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ISSPositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "timestamp")
    private long timestamp;

    @Column(name = "localDateTime")
    private LocalDateTime localDateTime;

}
