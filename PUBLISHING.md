# Publishing to GitHub

This folder is a preparation draft, not a published repository. The owner must choose the GitHub account, review its contents and license obligations, and decide to publish it publicly. The app's About screen currently says the Sobito repository has not been published; update that text and the source URL after creating a repository.

Before publishing:

1. Review README, privacy and security statements, all code changes, dependency versions and licenses. Opening source is not a security audit.
2. Run a dedicated secret scanner on the exact repository and review all results. Some upstream cryptography tests include public test keys; never publish real Firefox credentials, local account data, signing keys, access tokens, or debug keystores.
3. Enable private vulnerability reporting or establish a monitored private contact. Review how to distribute corresponding sources for all binary dependencies under their own licenses.
4. Review/record how any release APK was built and signed; do not represent a debug build as audited or reproducibly verified. Document tested devices, actual test results, limitations and known issues.

For an owner using GitHub CLI after the review, from this directory:

```bash
git init -b main
git add .
git status
# Configure your own Git identity if needed, then:
git commit -m "Initial experimental Sobito VPN source"
# Authenticates separately; use your own GitHub account.
gh auth login
gh repo create Sobito-VPN --public --source=. --remote=origin --push
```

Before running `gh repo create`, confirm the correct account, repository name, public visibility, and the files to be shared. Do not paste tokens or passwords into chat. Alternatively create an empty public repo on GitHub and follow its instructions to push this local project.
