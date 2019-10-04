/*import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class InputStudentGrades 
{
	//hashtable which stores the students
	private BucketHashTable<String, Student> ht = new BucketHashTable<>(new SimpleStringHasher());
	
	//goes through args string array for filenames
	public void inputStudentInfo(String[] filenames)
	{
		for(int i = 0; i < filenames.length; i++)
		{
			readAndInputFileData(filenames[i]);
		}
	}
	
	private void readAndInputFileData(String filename)
	{
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			ArrayList<Integer> maxScores = new ArrayList<Integer>();
			String line = br.readLine();
			int lineNumber = 1;
			String fileType = new String();
			while(line != null)
			{
				String[] elements = line.split(",");
				//figure out if this file contains homework, quiz, or exam scores.
				if((lineNumber - 1) == 0)
					fileType = elements[3].replaceAll("[0-9]", "");
				else if((lineNumber - 1) == 1)
				{
					//setting the max scores (slot 0 is total and 1 through ... are individual assigment totals
					for (int i = 2; i < elements.length; i++)
					{
						maxScores.add(tryToParseInt(elements[i]));
					}
				}
				else
				{
					//if the student isn't already in the hashtable
					if(ht.containsElement(elements[0]) == false)
					{
						String studentName = elements[1].replaceAll("\"", "") + ", " + elements[2].replaceAll("\"", "");
						int studentID = Integer.valueOf(elements[0]);
						Student newStudent = new Student(studentID, studentName);
						ht.addElement(elements[0], newStudent);
					}
					switch(fileType)
					{
						//for homework
						case "HW":
						{
							for (int i = 1; i < maxScores.size(); i++)
								ht.getElement(elements[0]).addHomeworkMax(maxScores.get(i));
							for(int i = 4; i < elements.length; i++)
								ht.getElement(elements[0]).addHomework(tryToParseInt(elements[i]));
							break;
						}
						//for quizzes
						case "Quiz":
						{
							for (int i = 1; i < maxScores.size(); i++)
								ht.getElement(elements[0]).addQuizMax(maxScores.get(i));
							for(int i = 4; i < elements.length; i++)
								ht.getElement(elements[0]).addQuiz(tryToParseInt(elements[i]));
							break;
						}
						//for exams
						case "E":
						{
							for (int i = 1; i < maxScores.size(); i++)
								ht.getElement(elements[0]).addExamMax(maxScores.get(i));
							for(int i = 4; i < elements.length; i++)
								ht.getElement(elements[0]).addExam(tryToParseInt(elements[i]));
							break;
						}
					}
					ht.getElement(elements[0]).refreshFinalGrade();
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
	
	public int tryToParseInt(String str)
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
}*/