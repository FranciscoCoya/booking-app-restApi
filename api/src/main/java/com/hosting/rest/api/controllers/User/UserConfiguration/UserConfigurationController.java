package com.hosting.rest.api.controllers.User.UserConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hosting.rest.api.models.User.UserConfiguration.UserConfigurationModel;
import com.hosting.rest.api.services.User.UserConfiguration.UserConfigurationServiceImpl;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/config")
public class UserConfigurationController {

	@Autowired
	private UserConfigurationServiceImpl userConfigurationService;

	@PostMapping("new")
	public UserConfigurationModel addNewUserConfiguration(
			@RequestBody final UserConfigurationModel userConfigurationToAdd) {
		return userConfigurationService.addNewUserConfiguration(userConfigurationToAdd);
	}

	@PutMapping
	public UserConfigurationModel udpateUserConfiguration(@PathVariable(name = "userId") final String userId,
			@RequestBody final UserConfigurationModel userConfigurationToUpdate) {
		UserConfigurationModel userConfigurationToReturn = null;

		try {
			userConfigurationToReturn = userConfigurationService.updateUserConfiguration(Integer.parseInt(userId),
					userConfigurationToUpdate);
		} catch (NumberFormatException nfe) {
			// TODO: handle exception
		}

		return userConfigurationToReturn;
	}

	@DeleteMapping("{userConfigId}")
	public void deleteUserConfiguration(@PathVariable(name = "userConfigId") final String userConfigurationId) {
		try {
			userConfigurationService.deleteUserConfiguration(Integer.parseInt(userConfigurationId));
		} catch (NumberFormatException nfe) {
			// TODO: handle exception
		}
	}

	@DeleteMapping("u/{userId}")
	public void deleteUserConfigurationByUserId(@PathVariable(name = "userId") final String userId) {
		try {
			userConfigurationService.deleteUserConfigurationByUserId(Integer.parseInt(userId));
		} catch (NumberFormatException nfe) {
			// TODO: handle exception
		}
	}

	@GetMapping("u/{userId}")
	public UserConfigurationModel findByUserId(@PathVariable(name = "userId") final String userId) {
		UserConfigurationModel userConfigurationToReturn = null;

		try {
			userConfigurationToReturn = userConfigurationService.findByUserId(Integer.parseInt(userId));
		} catch (NumberFormatException nfe) {
			// TODO: handle exception
		}
		return userConfigurationToReturn;
	}
}