package com.hosting.rest.api.models.Search;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hosting.rest.api.models.User.UserModel;

import lombok.Data;

@Entity
@Data
@Table(name = "USER_SEARCH_HISTORY")
public class UserSearchHistoryModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer idUserSearchHistory;

	@Column(name = "ID_USER")
	private UserModel idUser;

	@Column(name = "ID_SEARCH")
	private SearchModel idSearch;
}