import datetime

class Person():
    """Person object for test purposes"""
    def __init__(self, first_name="Fred", last_name="Freddington", DoB="01.01.1970", PoB="Freddingsburgh", *args, **kwargs) -> None:
        self.first_name = first_name
        self.last_name = last_name
        self.dob = DoB
        self.update_age()
        self.pob = PoB
        self.hobbies = kwargs
        if len(self.hobbies) == 0:
            self.hobbies["default"] = "..not much"
            
    def introduce(self):
        print(f"Hi, I'm {self.first_name} {self.last_name} ({self.age}) and came from {self.pob}. I'm interested in {self.hobbies.popitem()[1]}.")
            
    def update_age(self):
        self.dob = datetime.datetime.strptime(self.dob, "%d.%m.%Y") # type: ignore
        self.age = round((datetime.datetime.now() - self.dob).days/365)



felix = Person("Felix", DoB="28.11.2001", hobby="Tech", hobby2="Tech2")
felix.introduce()

class Student(Person):
    """Student class inheriting from Person"""
    def __init__(self, first_name="Fred", last_name="Freddington", DoB="01.01.1970", PoB="Freddingsburgh", matriculation=0000, semester=0, *args, **kwargs) -> None:
        super().__init__(first_name, last_name, DoB, PoB, *args, **kwargs)
        self.matriculation = matriculation
        self.semester = semester