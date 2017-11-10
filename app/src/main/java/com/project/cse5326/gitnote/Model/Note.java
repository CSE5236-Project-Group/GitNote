package com.project.cse5326.gitnote.Model;

import java.util.Date;

/**
 * Created by sifang on 11/4/17.
 */

/*
    Note:
	int number
	String title
	String body
	Date created_at
	String comment

Comment:
	String body
	User user
	Date commentAt

Event:
	int id
	enum event
	Date createAt

Label:
	int id
	String name
	(create: color)

Milestone:
	int id
	String title

Repo:
	int id
	String name
*/

public class Note {

    public String id;
    public String number;
    public String title;
    public String body;
    public String created_at;
    public String comment; // url
    public Label label;
}
