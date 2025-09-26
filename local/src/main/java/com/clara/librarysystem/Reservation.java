package com.clara.librarysystem;

public class Reservation {
    private int id;
    private String name;
    private String email;
    private String qcuian;
    private String room;
    private int numStudents;
    private String reservationDate;
    private String time;
    private String purpose;

    public Reservation(int id, String name, String email, String qcuian, String room,
                       int numStudents, String reservationDate, String time, String purpose) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.qcuian = qcuian;
        this.room = room;
        this.numStudents = numStudents;
        this.reservationDate = reservationDate;
        this.time = time;
        this.purpose = purpose;
    }

    public Reservation(String name, String email, String qcuian, String room,
                       int numStudents, String reservationDate, String time, String purpose) {
        this(0, name, email, qcuian, room, numStudents, reservationDate, time, purpose);
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getQcuian() { return qcuian; }
    public void setQcuian(String qcuian) { this.qcuian = qcuian; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public int getNumStudents() { return numStudents; }
    public void setNumStudents(int numStudents) { this.numStudents = numStudents; }

    public String getReservationDate() { return reservationDate; }
    public void setReservationDate(String reservationDate) { this.reservationDate = reservationDate; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", qcuian='" + qcuian + '\'' +
                ", room='" + room + '\'' +
                ", numStudents=" + numStudents +
                ", reservationDate='" + reservationDate + '\'' +
                ", time='" + time + '\'' +
                ", purpose='" + purpose + '\'' +
                '}';
    }
}
