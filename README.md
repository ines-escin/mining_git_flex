mining_git
==========

Java program to search for GitHub repositories using Gherkin-based acceptance test tool, such as Cucumber, that maintain a link among tasks and code changes. Such a link is defined by the occurrence of task ID in commit messages. That is, if it possible to identify changes at the production code and at the test code related to a same task, which in turn is identified by an ID. The task is a programming activity, such as the development of a new feature, a feature change, bug fix or refactoring.

Search mechanism
-
The search mechanism is very simple. The program takes as input the search criteria: project id (id of the repository to run the queries under Google BigQuery service) and programming language.
As result, the program outputs a csv file (/input/projects.csv) containing the URL (column repository_url) and master branch (column repository_master_branch) of found repositories.
Then, the program downloads each repository's source code (a zip file) and verifies if it contains feature files (extension .feature). If a repository does contain feature files, the program downloads all repository data and searches for commit messages containing task ID.
Finally, for each found commit, the program identifies the changed code (production and test code) by GitMiner and Gremlin. 

The output could be accessed in /output folder.

More about Gherkin and Cucumber: https://github.com/cucumber/cucumber/wiki/Gherkin.
More about Google BigQuery: https://cloud.google.com/bigquery/what-is-bigquery.
More about GitMiner:https://github.com/pridkett/gitminer
More about Gremlin:https://github.com/tinkerpop/gremlin/wiki

Requirement to use Google BigQuery API
-
The environment variable GOOGLE_APPLICATION_CREDENTIALS is checked. If this variable is specified it should point to a file that defines the credentials. The simplest way to get a credential for this purpose is to create a service account using the Google Developers Console in the section APIs & Auth, in the sub-section Credentials. Create a service account or choose an existing one and select Generate new JSON key. Set the environment variable to the path of the JSON file downloaded.

https://developers.google.com/identity/protocols/application-default-credentials

Requirement to use GitMiner
-
