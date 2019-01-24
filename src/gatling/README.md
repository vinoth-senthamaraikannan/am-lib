# How to run Gatling locally
### Prerequisites


#### Gatling Setup
1. Download and extract the [Gatling-2.3.1-bundle](https://repo1.maven.org/maven2/io/gatling/highcharts/gatling-charts-highcharts-bundle/2.3.1/gatling-charts-highcharts-bundle-2.3.1-bundle.zip) (currently used version in jenkins)
2. Copy bin and lib directories into $project_dir/src/gatling

#### IDE Setup
1. In intelliJ Right click the lib directory and select "Add as Library..."
2. In intelliJ Right click the $project_dir/src/gatling/simulations directory and mark it as Sources root

### Running
1. Start the am-lib project with the dummy service running - for more info check the project's Readme
2. Run Gatling from the /src/gatling/bin folder and follow the instructions
```bash
gatling.sh
```