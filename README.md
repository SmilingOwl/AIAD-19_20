# AIAD- 2019/2020

Projects for the Agents and Distributed Artificial Intelligence (AIAD) class of the Master in Informatics and Computer Engineering (MIEIC) at the Faculty of Engineering of the University of Porto (FEUP).

Made in colaboration with *Helena Montenegro*.

# First Project - Intelligent Factory (Summary)

Our project consists of a factory that has **machines** and receives **orders**. 
An **order** has a list of tasks that the machines need to do in order to produce a product. There is a need to allocate machines capable of answering to the orders, while minimizing the time needed for each order’s production. 

Each **machine** has a role which only makes it able to perform a specific task. The average time per task of a machine can be different for machines with the same role. The machines can lie about the time they take to fulfill a task. For that, there are two factors taken into account: proactivity and honesty. Proactive machines lie in order to decrease their execution time and machines with low proactivity value lie in order to increase it.

Each **order** has a certain number of credits which are awarded to the machines that complete one of its tasks. When an order sees that the machines with best finish time have times close to each other, it tries to negotiate with them by giving more credits so that the machines decrease their execution time.

[For more details](https://github.com/SmilingOwl/AIAD-19_20/tree/master/Project%201/Docs/aiad.pptx).

# Second Project - Intelligent Factory - Classification and Regression (Summary)

The purpose of the second part of the project is to study the number of credits that the orders need to increase during the negotiation process so that the machines execute the task in a satisfying time, considering the independent variables that are accessible to the orders. Therefore we will answer the following questions:
*If the order gives more credits to the machine, will it execute the task at the desired time?
How does a machine’s execution time improve when the order increases the credits given during the negotiation process?*

[For more details](https://github.com/SmilingOwl/AIAD-19_20/tree/master/Project%201/Docs/aiad-2.pptx)
