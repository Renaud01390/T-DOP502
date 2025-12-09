folder('Tools') {
    description('Folder for miscellaneous tools.')
}

job('Tools/clone-repository') {
    description('Clone a Git repository specified by parameter.')

    parameters {
        stringParam('GIT_REPOSITORY_URL', '', 'Git URL of the repository to clone')
    }

    wrappers {
        preBuildCleanup()
    }

    steps {
        shell('git clone "$GIT_REPOSITORY_URL" .')
    }
}

job('Tools/SEED') {
    description('Seed job that creates a project job from given parameters.')

    parameters {
        stringParam('GITHUB_NAME', '', 'GitHub repository owner/repo_name')
        stringParam('DISPLAY_NAME', '', 'Display name for the generated job')
    }

    steps {
        dsl {
            external('job_dsl.groovy')
            removeAction('IGNORE')
        }
    }
}

def githubName = binding.variables.get('GITHUB_NAME')
def displayName = binding.variables.get('DISPLAY_NAME')

if (githubName && displayName) {

    job(displayName) {
        description("Job generated for ${githubName}")

        properties {
            githubProjectUrl("https://github.com/${githubName}")
        }

        scm {
            git("https://github.com/${githubName}.git")
        }

        triggers {
            scm('* * * * *')
        }

        wrappers {
            preBuildCleanup()
        }

        steps {
            shell('make fclean')
            shell('make')
            shell('make tests_run')
            shell('make clean')
        }
    }
}
