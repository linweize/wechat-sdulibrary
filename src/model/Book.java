package model;

public class Book {

	private String bookName;
	private String availableCount;
	private String address;
	private String publisher;
	private String year;

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getAvailableCount() {
		return availableCount;
	}

	public void setAvailableCount(String availableCount) {
		this.availableCount = availableCount;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return "Book [bookName=" + bookName + ", availableCount="
				+ availableCount + ", address=" + address + ", publisher="
				+ publisher + ", year=" + year + "]";
	}

}
