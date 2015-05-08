package com.model;

public class People {
String name;
String role;
int age;

public People(String name, String role, int age) {
	super();
	this.name = name;
	this.role = role;
	this.age = age;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getRole() {
	return role;
}
public void setRole(String role) {
	this.role = role;
}
public int getAge() {
	return age;
}
public void setAge(int age) {
	this.age = age;
}

}
