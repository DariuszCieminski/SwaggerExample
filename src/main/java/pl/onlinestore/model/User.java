package pl.onlinestore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import pl.onlinestore.model.enums.Role;
import pl.onlinestore.util.AddressConverter;
import pl.onlinestore.util.JsonViews.OrderDetailed;
import pl.onlinestore.util.JsonViews.UserDetailed;
import pl.onlinestore.util.JsonViews.UserSimple;
import pl.onlinestore.util.ValidationGroups;
import pl.onlinestore.validation.UniqueEmail;

@Entity(name = "Customer")
@UniqueEmail(groups = ValidationGroups.UserCreation.class)
@ApiModel(description = "Store's customer")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @ApiModelProperty(value = "A unique user identifier", readOnly = true, example = "1")
    @JsonView({UserDetailed.class, OrderDetailed.class})
    private Long id;

    @NotBlank(message = "User's name cannot be empty.")
    @ApiModelProperty(value = "User's name", required = true, example = "John", position = 1)
    @JsonView({UserSimple.class, OrderDetailed.class})
    private String name;

    @NotBlank(message = "User's surname cannot be empty.")
    @ApiModelProperty(value = "User's surname", required = true, example = "Smith", position = 2)
    @JsonView({UserSimple.class, OrderDetailed.class})
    private String surname;

    @Valid
    @Convert(converter = AddressConverter.class)
    @ApiModelProperty(notes = "Home address of the user", position = 4)
    @JsonView({UserSimple.class, OrderDetailed.class})
    private Address address;

    @NotBlank(message = "E-Mail address cannot be empty.")
    @Email
    @ApiModelProperty(value = "E-Mail address of the user", required = true, example = "john.smith@myemail.com", position = 3)
    @JsonView({UserSimple.class, OrderDetailed.class})
    private String email;

    @NotBlank(message = "User password cannot be empty.", groups = ValidationGroups.UserCreation.class)
    @Size(min = 8, message = "Password must have at least {min} characters.")
    @ApiModelProperty(value = "Password for user account. Will be encrypted after account creation.", required = true,
                      example = "myP@ssw0rd", position = 5)
    private String password;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "All roles the user has. Will be set automatically to USER when registering new account.",
                      required = true, position = 6)
    @JsonView(UserDetailed.class)
    private Set<Role> roles;

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.REMOVE)
    @ApiModelProperty(value = "All orders made by user.", position = 7)
    @JsonIgnore
    private Set<Order> orders;

    public User() {
    }

    public User(String name, String surname, String email, String password, Set<Role> roles) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                            getName(),
                            getSurname(),
                            getAddress(),
                            getEmail(),
                            getPassword(),
                            getRoles());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return getId().equals(user.getId());
    }
}