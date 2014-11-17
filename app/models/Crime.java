package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name = "Crime")
public class Crime extends Model{
	public String Crime_Type;
	public double lat;
	public double lon;
	public String Location;
	public String Time;
}
