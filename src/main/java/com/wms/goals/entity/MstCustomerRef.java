package com.wms.goals.entity;

import com.wms.goals.utility.EntityNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = EntityNames.MST_CUSTOMER)
@Data
public class MstCustomerRef {
    @Id
    @Column(name = EntityNames.MstCustomer.CUSTOMER_ID)
    private UUID customerId;

    @Column(name = EntityNames.MstCustomer.EMAIL)
    private String email;

    @Column(name = EntityNames.MstCustomer.ID_RISK_PROFILE)
    private UUID idRiskProfile;

    @Column(name = EntityNames.MstCustomer.NIK)
    private String nik;

    @Column(name = EntityNames.MstCustomer.POB)
    private String pob;

    @Column(name = EntityNames.MstCustomer.DOB)
    private LocalDate dob;
}

