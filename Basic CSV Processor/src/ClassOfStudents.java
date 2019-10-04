import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class ClassOfStudents 
{
	private BucketHashTable<String, Student> studentInfo = new BucketHashTable<>(new SimpleStringHasher());
	private BucketHashTable<String, String> assignmentNamesChecker = new BucketHashTable<>(new SimpleStringHasher());
	
	private ArrayList<String> studentIDsOfTheClass = new ArrayList<String>();
	private ArrayList<String> assignmentTypes = new ArrayList<String>();
	private ArrayList<String> assignmentNames = new ArrayList<String>();
	private ArrayList<String> assignmentCategories = new ArrayList<String>();
	private ArrayList<Integer> categoriesMaxScores = new ArrayList<Integer>();
	private ArrayList<Integer> assignmentMaxScores = new ArrayList<Integer>();

	private int totalPointsPossible = 0;
	
	//goes through each file from args and reads in the data.
	public void inputStudentInfo(String[] filenames)
	{
		//read in the info from each file
		for(int i = 0; i < filenames.length; i++)
		{
			readAndInputStudentInfo(filenames[i]);
		}
		calculateFinalGrades();
		calculateCategoriesMaxPoints();
	}
	//reads in the class/student info from the files
	private void readAndInputStudentInfo(String filename)
	{
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line = br.readLine();
			String assignmentType = new String();
			
			String[] line1Elements = new String[0];
			int lineNumber = 0;
			while(line != null)
			{
				//for info in lines 1 and 2
				String[] elements = line.split(",");
				if(lineNumber < 2)
				{
					line = br.readLine();
					line1Elements = line.split(",");
					if(lineNumber == 0)
					{
						String[] temp = filename.split("_");
						if(!assignmentCategories.contains(temp[0]))
							assignmentCategories.add(temp[0]);
						assignmentType = temp[0];
						for(int i = 3; i < elements.length; i++)
						{
							//check if the assignmentName already exists
							if(!assignmentNamesChecker.containsElement(elements[i]))
							{
								assignmentTypes.add(assignmentType);
								assignmentNames.add(elements[i]);
								assignmentNamesChecker.addElement(elements[i], elements[i]);
								assignmentMaxScores.add(tryToParseInt(line1Elements[i]));
							}
						}
						lineNumber += 2;
						line = br.readLine();
					}
				}
				//start adding new students/updating student info
				else
				{
					//if the student isn't already in the class
					if(!studentInfo.containsElement(elements[0]))
					{
						String studentName = elements[1].replaceAll("\"", "") + "," + elements[2].replaceAll("\"", "");
						int studentID = Integer.valueOf(elements[0]);
						studentIDsOfTheClass.add(elements[0]);
						Student newStudent = new Student(studentID, studentName);
						studentInfo.addElement(elements[0], newStudent);
					}
					//add the assignment scores to their corresponding student
					for(int i = 4; i < elements.length; i++)
						studentInfo.getElement(elements[0]).addAssignment(tryToParseInt(elements[i]));
				}
				line = br.readLine();
				lineNumber++;
			}
			br.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//to handle string conversions to int
	private int tryToParseInt(String str)
	{
		try
		{
			return Integer.parseInt(str);
		}
		catch(NumberFormatException nfe)
		{
			return 0;
		}
	}
	//calculate the final grade of each student after all info has been read
	private void calculateFinalGrades()
	{
		//total points possible added
		for(int i = 0; i < assignmentMaxScores.size(); i++)
		{
			totalPointsPossible += assignmentMaxScores.get(i);
		}
		//calculate and assign final grade (2 decimals)
		for(int i = 0; i < studentIDsOfTheClass.size(); i++)
		{
			studentInfo.getElement(studentIDsOfTheClass.get(i)).addUpTotalPointsEarned();
			double temp1 = studentInfo.getElement(studentIDsOfTheClass.get(i)).getTotalPointsEarned();
			double temp2 = totalPointsPossible;
			double temp3 = 100 * (temp1/temp2);
			temp3 = Double.parseDouble(String.format("%.2f", temp3));
			studentInfo.getElement(studentIDsOfTheClass.get(i)).setFinalGrade(temp3);
		}
	}
	
	//calculate max points per class
	private void calculateCategoriesMaxPoints()
	{
		for(int i = 0; i < assignmentCategories.size(); i++)
		{
			int temp = 0;
			for(int j = 0; j < assignmentTypes.size(); j++)
			{
				if(assignmentTypes.get(j) == assignmentCategories.get(i))
				{
					temp += assignmentMaxScores.get(j);
				}
			}
			categoriesMaxScores.add(temp);
		}
	}
	
	//calculate category total for summary
	private void calculateStudentCategoriesPointsEarned(Student stu)
	{
		for(int i = 0; i < assignmentCategories.size(); i++)
		{
			int temp = 0;
			for(int j = 0; j < assignmentTypes.size(); j++)
			{
				if(assignmentTypes.get(j) == assignmentCategories.get(i))
				{
					temp += stu.getAssignmentScore(j);
				}
			}
			stu.setCategoryScoreEarned(temp);
		}
	}
	
	//output class summary
	public void outputStudentSummary()
	{
		try
		{
			BufferedWriter brw = new BufferedWriter(new FileWriter("summary.csv"));
			//line 1 and 2 class static data
			String line1 = "\"" + "#ID" + "\"," + 
					"\"" + "Name"  + "\"," + 
					"\"" + "Final Grade" + "\",";
			for(int i = 0; i < assignmentCategories.size(); i++)
				line1 += "\"" + assignmentCategories.get(i) + "\","; 
			String line2 = "\"" + "\"," +
					"\"" + "Overall" + "\"," +
					"\"" + "\",";
			for(int i = 0; i < categoriesMaxScores.size(); i++)
				line2 += "\"" + categoriesMaxScores.get(i) + "\",";
			brw.write(line1);
			brw.newLine();
			brw.write(line2);
			brw.newLine();
			//write individual student summary data
			for(int i = 0; i < studentIDsOfTheClass.size(); i++)
			{
				
				Student tempStudent = studentInfo.getElement(studentIDsOfTheClass.get(i));
				calculateStudentCategoriesPointsEarned(tempStudent);
				String studentLine = "\"" + tempStudent.getStudentID() + "\"," + 
						"\"" + tempStudent.getName()  + "\"," + 
						"\"" + tempStudent.getFinalGrade() + "\",";
						for(int j = 0; j < categoriesMaxScores.size(); j++)
						{
							studentLine += "\"" + tempStudent.getCategoryScoreEarned(j) + "\",";
						}
				brw.write(studentLine);
				brw.newLine();
			}
			brw.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	//output class details
	public void outputStudentDetails()
	{
		try
		{
			BufferedWriter brw = new BufferedWriter(new FileWriter("details.csv"));
			//line 1 and 2 class static data
			String line1 = "\"" + "#ID" + "\"," + 
					"\"" + "Name"  + "\",";
			for(int i = 0; i < assignmentNames.size(); i++)
				line1 += "\"" + assignmentNames.get(i) + "\",";
			String line2 = "\"" + "\"," +
					"\"" + "Overall" + "\",";
			for(int i = 0; i < assignmentMaxScores.size(); i++)
				line2 += "\"" + assignmentMaxScores.get(i) + "\",";
			brw.write(line1);
			brw.newLine();
			brw.write(line2);
			brw.newLine();
			//individual student data
			for(int i = 0; i < studentIDsOfTheClass.size(); i++)
			{
				Student tempStudent = studentInfo.getElement(studentIDsOfTheClass.get(i));
				String studentLine = "\"" + tempStudent.getStudentID() + "\"," + 
						"\"" + tempStudent.getName()  + "\",";
				for(int j = 0; j < assignmentMaxScores.size(); j++)
					studentLine += "\"" + tempStudent.getAssignmentScore(j) + "\",";
				brw.write(studentLine);
				brw.newLine();
			}
			brw.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
