package com.hosting.rest.api.controllers.User;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hosting.rest.api.exceptions.IllegalArguments.IllegalArgumentsCustomException;
import com.hosting.rest.api.models.User.UserModel;
import com.hosting.rest.api.services.User.UserServiceImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Francisco Coya
 * @version v1.0.3
 * @apiNote Controlador para los usuarios de la aplicación.
 *
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

	@Autowired
	private UserServiceImpl userService;

	@PreAuthorize("hasRole('ROLE_BASE_USER') or hasRole('ROLE_HOST_USER') or hasRole('ROLE_ADMIN_USER')")
	@PostMapping("new")
	public UserModel addNewUser(@Valid @RequestBody final UserModel userToCreate) {
		return userService.addNewUser(userToCreate);
	}

	@PreAuthorize("hasRole('ROLE_BASE_USER') or hasRole('ROLE_HOST_USER') or hasRole('ROLE_ADMIN_USER')")
	@DeleteMapping("{userId}")
	public void deleteUserById(@PathVariable(value = "userId") final String userId) {
		try {
			userService.deleteUserById(Integer.parseInt(userId));

		} catch (NumberFormatException nfe) {
			log.error("El id del usuario [ " + userId + " ] no es válido.");
			throw new IllegalArgumentsCustomException("El id del usuario [ " + userId + " ] no es válido.");
		}
	}

	@PreAuthorize("hasRole('ROLE_BASE_USER') or hasRole('ROLE_HOST_USER') or hasRole('ROLE_ADMIN_USER')")
	@PutMapping("{userId}")
	public UserModel udpateUser(@PathVariable(name = "userId") final String userId,
			@RequestBody UserModel userModelToUpdate) {
		UserModel userToReturn = null;

		try {
			userToReturn = userService.updateUser(Integer.parseInt(userId), userModelToUpdate);

		} catch (NumberFormatException nfe) {
			log.error("El id del usuario no es un número.");
			throw new IllegalArgumentsCustomException("El id del usuario no es un número.");
		}
		return userToReturn;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN_USER')")
	@GetMapping("all/started")
	public List<UserModel> getAllStartedUsers() {
		return userService.findAllStartedUsers();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN_USER')")
	@GetMapping("all")
	public List<UserModel> getAllUsers() {
		return userService.findAllUsers();
	}

	@PreAuthorize("hasRole('ROLE_BASE_USER') or hasRole('ROLE_HOST_USER') or hasRole('ROLE_ADMIN_USER')")
	@GetMapping("{userId}")
	public UserModel getUserById(@PathVariable(value = "userId") final String userId) {
		UserModel userToReturn = null;

		try {
			userToReturn = userService.getUserById(Integer.parseInt(userId));

		} catch (NumberFormatException nfe) {
			log.error("El id del usuario ha de ser un valor numérico.");
			throw new IllegalArgumentsCustomException("El id del usuario ha de ser un valor numérico.");
		}

		return userToReturn;
	}

	@PreAuthorize("hasRole('ROLE_BASE_USER') or hasRole('ROLE_HOST_USER') or hasRole('ROLE_ADMIN_USER')")
	@GetMapping("load/{userEmail}")
	public UserDetails getUserByEmail(@PathVariable(name = "userEmail") final String emailToSearch) {
		return userService.loadUserByUsername(emailToSearch);
	}
}
