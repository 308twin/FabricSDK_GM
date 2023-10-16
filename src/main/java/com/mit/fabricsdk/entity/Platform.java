package com.mit.fabricsdk.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Haodong Li
 * @date 2023年05月30日 15:25
 */
@Entity
@Table(name = "blockChain_platform")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "platform_name", nullable = false)
    private String platformName;

    @Column(name = "platform_account", nullable = false)
    private String platformAccount;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "power", nullable = false)
    private String power;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "pathname", nullable = false)
    private int pathname;

    @Column(name = "channel")
    private String channel;

    @Column(name = "org_name", nullable = false)
    private String orgName;

    @Column(name = "server_name", nullable = false)
    private String serverName;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
