import java.util.ArrayList;

class Student {
	private int studentID;
	private String name; //"lastname, firstname"
	
	//homework/quiz/exam 1 will start at element 0
	private ArrayList<Integer> assignmentScores = new ArrayList<Integer>();
	private ArrayList<Integer> categoryScoreEarned = new ArrayList<Integer>();
	private int totalPointsEarned = 0;
	private double finalGrade = 0;
	
	//default constructor
	public Student(){}
	
	//constructor with studentID and name
	public Student(int id, String studentName)
	{
		this.setStudentID(id);
		this.setName(studentName);
	}
	
	//retrieve studentID, name, earned values, and max values
	public int getStudentID() 
	{
		return studentID;
	}
	public String getName()
	{
		return name;
	}
	public int getAssignmentScore(int num)
	{
		return assignmentScores.get(num);
	}
	public int getTotalPointsEarned()
	{
		return totalPointsEarned;
	}
	public double getFinalGrade()
	{
		return finalGrade;
	}
	public int getCategoryScoreEarned(int num)
	{
		return categoryScoreEarned.get(num);
	}
	
	//set studentID, name, earned values, and max values
	public void setStudentID(int id)
	{
		this.studentID = id;
	}
	public void setName(String readName)
	{
		this.name = readName;
	}
	public void setAssignmentScore(int score, int assignmentNumber)
	{
		this.assignmentScores.add(assignmentNumber, score);
	}
	public void setFinalGrade(double grade)
	{
		this.finalGrade = grade;
	}
	public void setCategoryScoreEarned(int num)
	{
		categoryScoreEarned.add(num);
	}
	
	public void addAssignment(int assignmentScore)
	{
		this.assignmentScores.add(assignmentScore);
	}
	
	public void addUpTotalPointsEarned()
	{
		totalPointsEarned = 0;
		for(int i = 0; i < assignmentScores.size(); i++)
			totalPointsEarned += assignmentScores.get(i);
	}
	
}
