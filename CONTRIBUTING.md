# Contributing to Wathe: Extended

These rules apply to everyone who contributes to Wathe: Extended. Read them in full before opening an issue or a pull request. Contributions that do not follow these rules may be closed without review.

## 1. Scope

Wathe: Extended is the core mod of [The Harpy Express: Extended](https://modrinth.com/modpack/the-harpy-express-extended) modpack and is not intended to be used outside of it.
- Issues from users running the mod outside the base modpack are **closed without investigation**.
- Pull requests are accepted for **code** (features and bug fixes) and **translations** only.
- Pull requests containing textures, models, or sounds are also accepted only if the necessary credits are provided.

## 2. Issues

> [!WARNING]
> Before creating an issue, please search both open and closed issues before submitting. Duplicates will be closed.

**Every issue must include:**
- Both **Wathe: Extended** mod and **The Harpy Express: Extended** modpack versions
- Whether the issue occurs on the client, the server, or both
- Numbered steps to reproduce, and the expected / actual behavior
- The relevant log or crash report, attached as a file or paste link
- Any non-default Wathe: Extended configuration relevant to the bug

Issues that have missing information may be closed. Exploits and crashes that can be abused on servers may be reported publicly as regular issues.

## 3. Pull Requests

> [!TIP]
> Pull requests may be opened without prior discussion and will be reviewed at the maintainers discretion. Large or design-altering changes may be declined regardless of code quality, so opening an issue first is advisable for these types of PRs.

**Every Pull Request must:**
1. Target the `main` branch.
2. Build successfully with the Gradle wrapper.
3. Be tested in-game on both the **client** and a **dedicated server**, where the change affects both.
4. Contain a single, focused change. Unrelated modifications must be submitted separately.
5. Describe what the change does, why it is needed, and how it was tested.

> [!NOTE]
>
> ### Testing environment
> The mod and the modpack are intended for use on dedicated servers.
> Server-side testing must therefore be performed on a real **dedicated server** (a standalone server process, separate from the game client). Worlds hosted from a client do not count as server testing. This includes singleplayer worlds, worlds opened with "Open to LAN", and worlds shared through "invites" or hosting mods such as Essential. These all run on the **integrated server**, which shares a process with the client, so client-only code leaking onto the server, missing synchronization, and packet handling problems can go unnoticed.

Pull requests that fail the build or were not tested will not be reviewed.

Reviewer feedback must be addressed within a reasonable time. Inactive pull requests may be closed.


## 4. Commit Messages

Commits must follow [Conventional Commits](https://www.conventionalcommits.org/):
```
<type>: <short description in the imperative mood>
```

Accepted types include `feat`, `change`, `compat`, `fix`, `refactor`, `docs`, `chore`, and `locale` for translations.

Examples:
```
fix: resolve null pointer in player position handler
feat: add configurable timeout to sync request logic
locale: add French translation
```

## 5. Code

- Follow the style and structure of the surrounding code.
- Gameplay logic must run on the server. The client is responsible only for display and input.
- Options that server owners may reasonably want to change must be exposed in the configuration screen, with a default that preserves existing behavior.
- Data that must survive reconnects or restarts must be serialized correctly.
- Remove debug output, dead code, and commented-out code before submitting.

> [!NOTE]
> Java code is formatted with [google-java-format](https://github.com/google/google-java-format). Please run it (or enable the IDE plugin) before opening a pull request so reviews can focus on the change itself.

## 6. Compatibility with Other Add-ons

If a bug originates in another add-on, it must be **reported to that add-on's author first**.
A fix may be submitted to this repository only if the upstream issue cannot be resolved in a reasonable time or if the said mod is no longer maintained. Such a pull request must:

- Name the affected add-on and version
- Link the upstream report
- Explain how to determine when the fix is no longer needed

Compatibility code must not prevent the mod from loading when the add-on is absent.
**Wathe: Murder Mystery** and **HarpyModLoader** are the only hard dependencies, any other add-on are and should always be optional dependencies.

## 7. Translations

New languages and corrections to existing languages are both accepted.

- Use the English language file as the source of truth and keep all keys unchanged.
- Do not alter placeholders (for example `%s`, `%d` or custom brackets such as `{icon:instinct}`).
- Do not alter newlines (like `\n`).
- Match the terminology already used in the existing translation of your language.

## 8. AI-Generated Code

AI-assisted contributions are permitted but **must be disclosed** in the pull request description, stating which tools were used and which parts of the change they produced. The contributor remains fully responsible for the correctness, testing, and licensing of all submitted code. Untested or unreviewed AI-generated changes will be rejected.

> [!CAUTION]
> ### Modrinth Content Rules notice
> Wathe: Extended is distributed on Modrinth and is bound by **Section 6** of the [Modrinth Content Rules](https://modrinth.com/legal/rules), which governs generative AI.

**In summary:**
- Projects must be honest about their use of generative AI in their production, publication, and within the project itself.
- Projects may not be entirely or primarily made up of AI-generated output.
- The "Contains AI-generated content" disclosure must be applied when a substantial portion of a project's code is a product of AI output. Modrinth's [help article](https://support.modrinth.com/en/articles/16551575-disclosure-and-usage-of-ai) extends this to other AI-generated material, such as assets, entire classes, translations, and project page text.
- Fully or primarily AI-generated images are not permitted anywhere on a project page.

**As a result:**
- Accurate disclosure is required so that the maintainer can keep the project's Modrinth disclosures correct. Contributions with undisclosed AI use may be rejected or reverted.
- Pull requests that are entirely or primarily AI-generated will be rejected.
- Translations produced with generative AI must be disclosed as such.

The Modrinth Content Rules may change over time. The version published by Modrinth at the time of
submission applies.

## 9. Licensing and Credit

This project is licensed under the [GNU General Public License v3.0](https://github.com/rezelyn/Wathe-Extended/blob/main/LICENSE). By submitting a contribution, you confirm that you have the right to submit it and agree that it will be distributed under the same license.

Merged pull requests will be recorded in the changelog of the release that includes them. The authors of merged contributions will also be listed in the Credits section of the README.
