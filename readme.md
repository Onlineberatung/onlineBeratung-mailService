# Caritas Online-Beratung MailService

The MailService provides REST API calls which trigger e-mail dispatching for users and also error notifications for system administrators.
The service must only be reachable from within the Docker network and therefore can only be triggered by other services and not externally (by the client).
It comes up with predefined templates but also a free text template which supports HTML elements.

## Help and Documentation
In the project [documentation](https://caritasdeutschland.github.io/documentation/docs/setup/setup-backend) you'll find information for setting up and running the project.
You can find some detailled information of the service architecture and its processes in the repository [documentation](https://github.com/CaritasDeutschland/caritas-onlineBeratung-mailService/tree/master/documentation).

## License
The project is licensed under the AGPLv3 which you'll find [here](https://github.com/CaritasDeutschland/caritas-onlineBeratung-mailService/blob/master/LICENSE).

## Code of Conduct
Please have a look at our [Code of Conduct](https://github.com/CaritasDeutschland/.github/blob/master/CODE_OF_CONDUCT.md) before participating in the community.

## Contributing
Please read our [contribution guidelines](https://github.com/CaritasDeutschland/.github/blob/master/CONTRIBUTING.md) before contributing to this project.
