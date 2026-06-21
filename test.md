<div class="card">
    <h1>Underground Simulator</h1>
    <p>CLI-Zugsimulatorspiel</p>

    <div class="buttons">
        <a class="btn btn-primary" href="<?= $releaseUrl ?>">
            Download Release <?php echo $latestRelease ?>
        </a>

        <?php if ($snapshotUrl): ?>
        <a class="btn btn-secondary" href="<?= $snapshotUrl ?>">
            Download Snapshot <?php echo $latestSnapshot ?>
        </a>
        <?php endif; ?>
    </div>

    <div class="version">
        Release: <?= $latestRelease ?><br>
        <?php if ($snapshotVersion): ?>
        Snapshot: <?= $snapshotVersion ?>
        <?php endif; ?>
    </div>
</div>