param(
    [string]$GitHubUser = 'mauricioas1',
    [string]$RepoName = 'dock-webhook'
)

Set-Location -Path (Split-Path -Parent $MyInvocation.MyCommand.Definition)

if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Error 'git is not installed. Please install Git first.'
    exit 1
}

if (-not (git rev-parse --is-inside-work-tree 2>$null)) {
    git init
}

git add .
git commit -m "chore: initial import - dock webhook receiver" -a 2>$null
git branch -M main

if (Get-Command gh -ErrorAction SilentlyContinue) {
    gh auth status 2>$null
    if ($LASTEXITCODE -ne 0) {
        gh auth login
    }
    gh repo create $GitHubUser/$RepoName --public --source=. --remote=origin --push || Write-Host 'Repository may already exist; ensure remote configured.'
} else {
    $remote = "https://github.com/$GitHubUser/$RepoName.git"
    git remote add origin $remote 2>$null
    git push -u origin main
}

Write-Host 'Push complete (verify on GitHub).'
