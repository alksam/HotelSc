package org.example.DAO;

import org.example.Entity.Role;
import org.example.Entity.User;

public interface ISecurityDAO {
  User createUser(String username, String password);
Role createRole(String role);
User addRoleToUser(String username, String role);

  User addUserRole(String username, String role);


}