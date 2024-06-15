package org.vgcs.model;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProcessedOrder {
    private String userPid;
    private String orderPid;
    private String supplierPid;
}
