package springdev.scm.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserForm {

    @NotBlank(message = "Name is required")
    @Size(message = "Min. 3 characters are required")
    private String name;
    @Email(message = "Invalid email address")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 4, message = "Min. 4 characters are required")
    private String password;
    @Size(min = 4, max = 10, message = "Invalid phone number")
    private String phoneNumber;
    // :> [testing] sending static value
    // @NotBlank(message = "About is required")
    private String about;
    private String profilePicture;

}
