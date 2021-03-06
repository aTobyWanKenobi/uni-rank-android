# ![Logo](https://github.com/aTobyWanKenobi/uni-rank-android/blob/master/screenshots/icon_app_logo.png) UniRank
## User generated rankings: an Android mobile application


## Description
Developed in the context of my Spring 2017 Bachelor semester project at the Media and Design Laboratory (LDM) at the École polytechnique fédérale de Lausanne (EPFL), UniRank is an application that allows to generate personalized university rankings based on a number of performance indicators.

The goal of the application is to provide non-expert users with ways to directly manipulate university data without having to rely on existing rankings, which are not free from transparency concerns. It does so by presenting the user with a set of indicators which constitute the data used by some of the most recognized annual university rankings. UniRank then uses an implementation of HodgeRank, a rank aggregation algorithm by Jiang et al. (2008), in order to allow users to combine individual rankings and indicators by associating a weigth to each one of them.

A more extensive PowerPoint presentation can be found [here](https://github.com/aTobyWanKenobi/uni-rank-android/blob/master/PowerPoint_UniRank.ppsx)

## Features

- **Rank aggregation procedure**

  Select indicators, assign weights and see the algorithm's results.
  
  ![Aggregation setup](/screenshots/Screen_CreationIndicators.JPG)
  ![Aggregation setup2](/screenshots/Screen_CreationDialog.JPG)
  ![Aggregation result](/screenshots/Screen_ConfrontResult.JPG)
  
- **Personal rankings saving system**

  Save work across application launches.
  
  ![Saves](/screenshots/Screen_SavePreview.JPG)
  
- **Aggregation comparison**

  Compare different generated rankings to inspect differences.
  
  ![Comparison](/screenshots/Screen_Comparison.jpg)
  
- **Ability to contribute to a shared pool of user-generated rankings**

  Upload interesting results to move towards an increasingly precise representation of what people value in universities.
  
- **Access to this crowd-sourced data repository to retrieve statistics and trends**

  Shared data is accessible from within the application as a research tool.
  
  ![Shared Pool Queries](/screenshots/Screen_SharedPoolQuery.jpg)
  ![Shared Pool Statistics](/screenshots/Screen_SharedPoolStatistics.jpg)

## Deployment
The application is still not in a finalized state and is not guaranteed to work on every Android device. Until the first beta release, UniRank can be cloned and opened as a project in Android Studio.
  
## Authors
Albergoni Tobia - _Developement_ - [aTobyWanKenobi](https://github.com/aTobyWanKenobi)

## License
MIT licensed, details in [LICENSE.md](https://github.com/aTobyWanKenobi/uni-rank-android/blob/master/LICENSE)

## Acknowledgments
- Koh Immanuel Chee Beng - _Project supervisor_ 
- Huang Jeffrey - _Supervising professor_

- _CityRank_ (Flaxman, Huang, Stephenson, Comtesse, 2009)
- _Statistical ranking and combinatorial Hodge theory_ (Jiang, Lim, Yao, Ye, 2008)
- Michael Thomas Flanagan's Java Scientific Library - https://www.ee.ucl.ac.uk/~mflanaga/java/Minimisation.html
- Jahoda Phillip, MPAndroidCharts - https://github.com/PhilJay/MPAndroidChart
