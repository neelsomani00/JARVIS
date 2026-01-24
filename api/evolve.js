export default async function handler(req, res) {
  // Use the environment variable from Vercel settings
  const token = process.env.JARVIS_GITHUB_TOKEN;
  const repo = "neelsomani00/JARVIS";

  if (!token) {
    return res.status(500).json({ error: "Vercel Error: JARVIS_GITHUB_TOKEN is not set." });
  }

  const { fileName, newContent, commitMessage } = req.body;

  try {
    // 1. Get the current file's SHA (Github requirement for updates)
    const getFile = await fetch(`https://api.github.com/repos/${repo}/contents/${fileName}`, {
      headers: { Authorization: `token ${token}` }
    });
    const fileData = await getFile.json();

    // 2. Push the code update to GitHub
    const response = await fetch(`https://api.github.com/repos/${repo}/contents/${fileName}`, {
      method: 'PUT',
      headers: { 
        Authorization: `token ${token}`,
        'Content-Type': 'application/json' 
      },
      body: JSON.stringify({
        message: commitMessage || "JARVIS Evolution: Code Update",
        content: Buffer.from(newContent).toString('base64'),
        sha: fileData.sha
      })
    });

    const result = await response.json();
    res.status(200).json({ status: "Success", commit: result.commit.html_url });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
}
